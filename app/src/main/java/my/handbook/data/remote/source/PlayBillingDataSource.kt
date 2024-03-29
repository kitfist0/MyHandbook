package my.handbook.data.remote.source

import android.app.Activity
import com.android.billingclient.api.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import my.handbook.BuildConfig
import my.handbook.data.remote.model.PlayBillingResponse
import my.handbook.data.remote.model.ProductInfo
import my.handbook.data.remote.model.onSuccess
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class PlayBillingDataSource @Inject constructor(
    billingClientBuilder: BillingClient.Builder,
) {

    private companion object {
        const val UNKNOWN_ERROR = "Unknown error"
    }

    private val billingResponses = Channel<PlayBillingResponse<List<String>>>(Channel.UNLIMITED)

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        val response = PurchasesResult(billingResult, purchases.orEmpty()).toPurchasedIdsResponse()
        billingResponses.trySend(response)
    }

    private val billingClient = billingClientBuilder
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    private val billingConnectionMutex = Mutex()

    suspend fun purchaseProduct(
        activity: Activity,
        productId: String,
    ): PlayBillingResponse<List<String>> {
        billingClient.ensureReady().exceptionOrNull()?.let { exception ->
            return PlayBillingResponse.Error(exception.message ?: UNKNOWN_ERROR)
        }
        val productDetailsResult = queryNonConsumableProductDetails()
        return productDetailsResult.productDetailsList
            ?.find { it.productId == productId }
            ?.let { productDetails ->
                val productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()
                billingClient.launchBillingFlow(activity, billingFlowParams)
                billingResponses.receive().also { response ->
                    response.onSuccess { acknowledgePurchasedProducts() }
                }
            }
            ?: PlayBillingResponse.Error(productDetailsResult.billingResult.getErrorMessage())
    }

    suspend fun getProductsInfo(): PlayBillingResponse<List<ProductInfo>> {
        billingClient.ensureReady().exceptionOrNull()?.let { exception ->
            return PlayBillingResponse.Error(exception.message ?: UNKNOWN_ERROR)
        }
        val productDetailsResult = queryNonConsumableProductDetails()
        return productDetailsResult.productDetailsList
            ?.map { productDetails ->
                ProductInfo(
                    id = productDetails.productId,
                    name = productDetails.name,
                )
            }
            ?.let { productsInfo -> PlayBillingResponse.Success(productsInfo) }
            ?: PlayBillingResponse.Error(productDetailsResult.billingResult.getErrorMessage())
    }

    suspend fun getIdsOfPurchasedProducts(): PlayBillingResponse<List<String>> {
        return billingClient.ensureReady().exceptionOrNull()
            ?.let { exception -> PlayBillingResponse.Error(exception.message ?: UNKNOWN_ERROR) }
            ?: queryNonConsumablePurchases().toPurchasedIdsResponse()
    }

    private suspend fun queryNonConsumableProductDetails(): ProductDetailsResult {
        val productList = BuildConfig.PRODUCT_IDS.map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        return billingClient.queryProductDetails(queryProductDetailsParams)
    }

    private suspend fun queryNonConsumablePurchases(): PurchasesResult {
        val queryPurchasesParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        return billingClient.queryPurchasesAsync(queryPurchasesParams)
    }

    private suspend fun acknowledgePurchasedProducts() {
        queryNonConsumablePurchases().purchasesList
            .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED && !it.isAcknowledged }
            .onEach {
                billingClient.acknowledgePurchase(
                    AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(it.purchaseToken)
                        .build()
                )
            }
    }

    /**
     * Returns immediately if this BillingClient is already connected, otherwise
     * initiates the connection and suspends until this client is connected.
     * If a connection is already in the process of being established, this
     * method just suspends until the billing client is ready
     */
    private suspend fun BillingClient.ensureReady(): Result<Boolean> =
        billingConnectionMutex.withLock {
            return runCatching {
                if (!isReady) {
                    connectOrThrow()
                }
                true
            }
        }

    private suspend fun BillingClient.connectOrThrow() = suspendCoroutine { continuation ->
        startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) {
                    if (result.isOk()) {
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWithException(RuntimeException(result.getErrorMessage()))
                    }
                }

                override fun onBillingServiceDisconnected() {
                    // No need to setup reconnection logic here, call ensureReady()
                    // before each purchase to reconnect as necessary
                }
            }
        )
    }

    private fun PurchasesResult.toPurchasedIdsResponse() =
        if (billingResult.isOk()) {
            val purchasedIds = purchasesList
                .filter { purchase -> purchase.purchaseState == Purchase.PurchaseState.PURCHASED }
                .flatMap { purchase -> purchase.products }
            PlayBillingResponse.Success(purchasedIds)
        } else {
            PlayBillingResponse.Error(billingResult.getErrorMessage())
        }

    private fun BillingResult.getErrorMessage() = when (responseCode) {
        BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> "Service timeout"
        BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> "Feature not supported"
        BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> "Service disconnected"
        BillingClient.BillingResponseCode.USER_CANCELED -> "User canceled"
        BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> "Service unavailable"
        BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> "Billing unavailable"
        BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> "Item unavailable"
        BillingClient.BillingResponseCode.DEVELOPER_ERROR -> "Developer error"
        BillingClient.BillingResponseCode.ERROR -> "Error"
        BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> "Item already owned"
        BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> "Item not owned"
        else -> UNKNOWN_ERROR
    }

    private fun BillingResult.isOk() = responseCode == BillingClient.BillingResponseCode.OK
}
