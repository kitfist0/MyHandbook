package my.handbook.data.remote

import android.app.Activity
import com.android.billingclient.api.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class PlayBillingDataSource @Inject constructor(
    private val billingClient: BillingClient,
) {

    private val billingConnectionMutex = Mutex()
    private val productDetailsMap = mutableMapOf<String, ProductDetails>()

    suspend fun queryProductDetails(productIds: List<String>): ProductDetailsResult {
        billingClient.ensureReady()
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                productIds.map { productId ->
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                }
            )
            .build()
        val productDetailsResult = billingClient.queryProductDetails(queryProductDetailsParams)
        productDetailsResult.productDetailsList?.let { list ->
            list.forEach { details -> productDetailsMap[details.productId] = details }
        }
        return productDetailsResult
    }

    suspend fun queryPurchases(): PurchasesResult {
        billingClient.ensureReady()
        val queryPurchasesParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        return billingClient.queryPurchasesAsync(queryPurchasesParams)
    }

    fun purchaseProduct(activity: Activity, productId: String): Boolean {
        val productDetails = productDetailsMap[productId]
        return if (billingClient.isReady && productDetails != null) {
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
            true
        } else {
            false
        }
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

    private suspend fun BillingClient.connectOrThrow() = suspendCoroutine<Unit> { cont ->
        startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.isOk()) {
                    cont.resume(Unit)
                } else {
                    cont.resumeWithException(
                        RuntimeException("Billing conn failed: ${result.debugMessage}")
                    )
                }
            }

            override fun onBillingServiceDisconnected() {
                // No need to setup reconnection logic here, call ensureReady()
                // before each purchase to reconnect as necessary
            }
        })
    }

    private fun BillingResult.isOk() = responseCode == BillingClient.BillingResponseCode.OK
}
