package simple.billing.core

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.android.billingclient.api.*
import kotlinx.coroutines.*
import simple.billing.BuildConfig
import simple.billing.data.repository.ProductRepository
import kotlin.coroutines.CoroutineContext

class BillingHandler(
    private val context: Context,
    private val productRepository: ProductRepository
) : LifecycleObserver, PurchasesUpdatedListener, CoroutineScope,
    BillingClientStateListener, BillingUseCases {

    private var billingClient: BillingClient? = null
    private var lifecycle: Lifecycle? = null

    fun initBilling(lifecycle: Lifecycle) {
        this.lifecycle = lifecycle
        this.lifecycle?.addObserver(this)
    }

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.IO

    override val products = productRepository.productsFlow.asLiveData()

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
        billingClient = BillingClient.newBuilder(context)
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
        Log.d(LOG_TAG, "queryInAppPurchasesWithDetails")
        querySkuDetails()
        billingClient?.queryPurchases(BillingClient.SkuType.INAPP)?.purchasesList
            ?.let {
                BillingGlobal.HAS_PURCHASED_PRODUCTS = it.any { purchase -> purchase.statePurchased() }
                it.forEach { purchase -> purchase.handlePurchase() }
            }
    }

    private suspend fun querySkuDetails() {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(BuildConfig.ALL_PRODUCT_IDS.toList())
            .setType(BillingClient.SkuType.INAPP)
            .build()
        val result = billingClient?.querySkuDetails(params)
        Log.d(LOG_TAG, "querySkuDetails, responseCode: ${result?.billingResult?.responseCode}")
        result?.billingResult?.onOkBillingResult {
            result.skuDetailsList?.let {
                Log.d(LOG_TAG, "setupProducts")
                productRepository.setupProducts(it)
            }
        }
    }

    private suspend fun Purchase.handlePurchase() = takeIf { this.statePurchased() }
        ?.let { purchase ->
            Log.d(LOG_TAG, "handlePurchase, ${purchase.sku}")
            productRepository.onProductPurchased(purchase)
            BillingGlobal.HAS_PURCHASED_PRODUCTS = true
            if (!isAcknowledged) {
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchaseToken)
                    .build()
                billingClient?.acknowledgePurchase(params)?.onOkBillingResult {
                    Log.d(LOG_TAG, "acknowledgePurchase, success")
                }
            }
        }

    companion object {

        @Volatile private var instance: BillingHandler? = null

        fun getInstance(context: Context): BillingHandler {
            return instance ?: synchronized(this) {
                instance ?: BillingHandler(context, ProductRepository.getInstance(context))
                    .also { instance = it }
            }
        }

        private const val LOG_TAG = "BILLING_HANDLER"

        private inline fun BillingResult.onOkBillingResult(onOkResult: () -> Unit) =
            takeIf { responseCode == BillingClient.BillingResponseCode.OK }
                ?.let { onOkResult.invoke() }

        private fun Purchase.statePurchased(): Boolean =
            purchaseState == Purchase.PurchaseState.PURCHASED
    }
}
