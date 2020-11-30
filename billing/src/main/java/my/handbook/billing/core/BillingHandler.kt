package my.handbook.billing.core

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.android.billingclient.api.*
import kotlinx.coroutines.*
import my.handbook.billing.data.repository.ProductRepository
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class BillingHandler @Inject constructor(
    private val application: Application,
    private val productRepository: ProductRepository,
    @Named("skuList") private val skuList: List<String>
) : LifecycleObserver, PurchasesUpdatedListener, CoroutineScope,
    BillingClientStateListener, BillingUseCases {

    companion object {
        private const val LOG_TAG = "BILLING_HANDLER"

        private inline fun BillingResult.onOkBillingResult(onOkResult: () -> Unit) =
            takeIf { responseCode == BillingClient.BillingResponseCode.OK }
                ?.let { onOkResult.invoke() }
    }

    private var billingClient: BillingClient? = null
    private var lifecycle: Lifecycle? = null

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.IO
    override val products = productRepository.productsFlow.asLiveData()

    override fun initBilling(lifecycle: Lifecycle) {
        this.lifecycle = lifecycle
        this.lifecycle?.addObserver(this)
    }

    override fun purchaseProduct(activity: Activity, originalJson: String) {
        takeIf { billingClient?.isReady == true }
            ?.let {
                Log.d(LOG_TAG, "launchBillingFlow, billing flow is ready")
                val params = BillingFlowParams.newBuilder()
                    .setSkuDetails(SkuDetails(originalJson))
                    .build()
                billingClient?.launchBillingFlow(activity, params)
            }
            ?: Log.d(LOG_TAG, "launchBillingFlow, billing flow isn't ready")
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_CREATE)
    private fun startConnection() {
        Log.d(LOG_TAG, "startConnection")
        billingClient = BillingClient.newBuilder(application)
            .setListener(this)
            .enablePendingPurchases()
            .build()
        billingClient?.startConnection(this)
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    private fun endConnection() {
        Log.d(LOG_TAG, "endConnection")
        billingClient?.endConnection()
        lifecycle?.removeObserver(this)
    }

    override fun onBillingSetupFinished(result: BillingResult) {
        Log.d(LOG_TAG, "onBillingSetupFinished, ${result.responseCode}")
        takeIf { result.responseCode == BillingClient.BillingResponseCode.OK }
            ?.launch { queryInAppPurchasesWithDetails() }
    }

    override fun onBillingServiceDisconnected() {
        Log.d(LOG_TAG, "onBillingServiceDisconnected")
        if (lifecycle?.currentState == Lifecycle.State.CREATED) {
            billingClient?.startConnection(this)
        }
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        Log.d(LOG_TAG, "onPurchasesUpdated, ${result.debugMessage}")
        takeIf { result.responseCode == BillingClient.BillingResponseCode.OK }
            ?.launch { purchases?.forEach { it.handlePurchase() } }
    }

    private suspend fun queryInAppPurchasesWithDetails() {
        querySkuDetails()
        billingClient?.queryPurchases(BillingClient.SkuType.INAPP)?.purchasesList
            ?.let { it.forEach { purchase -> purchase.handlePurchase() } }
    }

    private suspend fun querySkuDetails() {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)
            .build()
        val result = billingClient?.querySkuDetails(params)
        result?.billingResult?.onOkBillingResult {
            result.skuDetailsList?.let { productRepository.setupProducts(it) }
        }
    }

    private suspend fun Purchase.handlePurchase() =
        takeIf { purchaseState == Purchase.PurchaseState.PURCHASED }
            ?.let { purchase ->
                if (isAcknowledged) {
                    productRepository.productPurchased(purchase)
                } else {
                    val params = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchaseToken)
                        .build()
                    billingClient?.acknowledgePurchase(params)?.onOkBillingResult {
                        productRepository.productPurchased(purchase)
                    }
                }
            }
}
