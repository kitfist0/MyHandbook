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
    private val clientBuilder: BillingClient.Builder,
    private val productRepository: ProductRepository,
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
        billingClient = clientBuilder
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

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        Log.d(LOG_TAG, "onBillingSetupFinished, ${billingResult.responseCode}")
        billingResult.onOkBillingResult {
            launch {
                querySkuDetails()
                billingClient?.queryPurchasesAsync(BillingClient.SkuType.INAPP)?.purchasesList
                    ?.handlePurchases()
            }
        }
    }

    override fun onBillingServiceDisconnected() {
        Log.d(LOG_TAG, "onBillingServiceDisconnected")
        if (lifecycle?.currentState == Lifecycle.State.CREATED) {
            billingClient?.startConnection(this)
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        Log.d(LOG_TAG, "onPurchasesUpdated, ${billingResult.debugMessage}")
        billingResult.onOkBillingResult {
            launch { purchases?.handlePurchases() }
        }
    }

    private suspend fun querySkuDetails() {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(BuildConfig.PRODUCT_IDS.toList())
            .setType(BillingClient.SkuType.INAPP)
            .build()
        val skuDetailsResult = billingClient?.querySkuDetails(params)
        Log.d(LOG_TAG, "querySkuDetails, responseCode: ${skuDetailsResult?.billingResult?.responseCode}")
        skuDetailsResult?.billingResult?.onOkBillingResult {
            skuDetailsResult.skuDetailsList?.let { productRepository.setupProducts(it) }
        }
    }

    private suspend fun List<Purchase>.handlePurchases() = forEach { purchase ->
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            Log.d(LOG_TAG, "handlePurchase, ${purchase.originalJson}")
            productRepository.onProductPurchased(purchase)
            BillingGlobal.HAS_PURCHASED_PRODUCTS = true
            if (!purchase.isAcknowledged) {
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient?.acknowledgePurchase(params)
            }
        }
    }

    companion object {

        @Volatile private var instance: BillingHandler? = null

        fun getInstance(context: Context): BillingHandler {
            return instance ?: synchronized(this) {
                instance ?: BillingHandler(
                    BillingClient.newBuilder(context),
                    ProductRepository.getInstance(context)
                ).also { instance = it }
            }
        }

        private const val LOG_TAG = "BILLING_HANDLER"

        private inline fun BillingResult.onOkBillingResult(onOkResult: () -> Unit) =
            takeIf { responseCode == BillingClient.BillingResponseCode.OK }
                ?.let { onOkResult.invoke() }
    }
}
