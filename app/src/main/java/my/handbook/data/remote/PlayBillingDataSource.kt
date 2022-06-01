package my.handbook.data.remote

import android.app.Activity
import com.android.billingclient.api.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import my.handbook.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class PlayBillingDataSource @Inject constructor(
    billingClientBuilder: BillingClient.Builder,
) {

    private val purchasesUpdatesChannel: Channel<PurchasedIdsResponse> = Channel(Channel.UNLIMITED)

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.isOk() && purchases?.isNotEmpty() == true) {
            val purchasedIds = purchases
                .filter { purchase -> purchase.purchaseState == Purchase.PurchaseState.PURCHASED }
                .flatMap { purchase -> purchase.products }
            purchasesUpdatesChannel.trySend(PurchasedIdsResponse.Success(purchasedIds))
        } else {
            purchasesUpdatesChannel.trySend(PurchasedIdsResponse.Error(billingResult.getErrorMessage()))
        }
    }

    private val billingClient = billingClientBuilder
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    private val billingConnectionMutex = Mutex()

    suspend fun purchaseProduct(activity: Activity, productId: String): PurchasedIdsResponse {
        billingClient.ensureReady()
        return queryProductDetailsList()
            .find { it.productId == productId }
            ?.let { productDetails ->
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(
                        listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build()
                        )
                    )
                    .build()
                billingClient.launchBillingFlow(activity, billingFlowParams)
                purchasesUpdatesChannel.receive()
            }
            ?: PurchasedIdsResponse.Error("Product id not found")
    }

    suspend fun acknowledgePurchases() {
        queryPurchasesList()
            .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED && !it.isAcknowledged }
            .onEach {
                billingClient.acknowledgePurchase(
                    AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(it.purchaseToken)
                        .build()
                )
            }
    }

    suspend fun queryProductDetailsList(): List<ProductDetails> {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                BuildConfig.PRODUCT_IDS.toList().map { productId ->
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                }
            )
            .build()
        return billingClient.queryProductDetails(params).productDetailsList.orEmpty()
    }

    suspend fun queryPurchasesList(): List<Purchase> {
        val queryPurchasesParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        return billingClient.queryPurchasesAsync(queryPurchasesParams).purchasesList
    }

    /**
     * Returns immediately if this BillingClient is already connected, otherwise
     * initiates the connection and suspends until this client is connected.
     * If a connection is already in the process of being established, this
     * method just suspends until the billing client is ready
     */
    private suspend fun BillingClient.ensureReady() = billingConnectionMutex.withLock {
        // Avoid suspension if another coroutine already connected
        if (isReady) {
            return
        }
        connectOrThrow()
    }

    private suspend fun BillingClient.connectOrThrow() = suspendCoroutine<Unit> { continuation ->
        startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) {
                    if (result.isOk()) {
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWithException(
                            RuntimeException("Billing conn failed: ${result.debugMessage}")
                        )
                    }
                }

                override fun onBillingServiceDisconnected() {
                    // No need to setup reconnection logic here, call ensureReady()
                    // before each purchase to reconnect as necessary
                }
            }
        )
    }

    private fun BillingResult.isOk() = responseCode == BillingClient.BillingResponseCode.OK

    private fun BillingResult.getErrorMessage() = when (responseCode) {
        BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> "Service timeout"
        BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> "Feature not supported"
        BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> "Service disconnected"
        BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> "Service unavailable"
        BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> "Billing unavailable"
        BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> "Item unavailable"
        BillingClient.BillingResponseCode.DEVELOPER_ERROR -> "Developer error"
        BillingClient.BillingResponseCode.ERROR -> "Error"
        else -> "Unknown error"
    }
}
