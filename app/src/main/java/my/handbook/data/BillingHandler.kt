package my.handbook.data

import android.app.Activity
import androidx.lifecycle.*
import com.android.billingclient.api.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import my.handbook.BuildConfig
import my.handbook.data.entity.Product
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class BillingHandler @Inject constructor(
    private val clientBuilder: BillingClient.Builder,
    private val productRepository: ProductRepository,
) : LifecycleObserver, PurchasesUpdatedListener, CoroutineScope, BillingClientStateListener {

    private var billingClient: BillingClient? = null
    private var lifecycle: Lifecycle? = null

    // Products
    val products: Flow<List<Product>> = productRepository.productsFlow

    // Error messages
    private val _errors = MutableSharedFlow<String>()
    val errors = _errors.asSharedFlow()

    fun initBilling(lifecycle: Lifecycle) {
        this.lifecycle = lifecycle
        this.lifecycle?.addObserver(this)
    }

    override val coroutineContext: CoroutineContext = Job()

    fun purchaseProduct(activity: Activity, originalJson: String) {
        takeIf { billingClient?.isReady == true }
            ?.let {
                val params = BillingFlowParams.newBuilder()
                    .setSkuDetails(SkuDetails(originalJson))
                    .build()
                billingClient?.launchBillingFlow(activity, params)
            }
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_CREATE)
    private fun startConnection() {
        billingClient = clientBuilder
            .setListener(this)
            .enablePendingPurchases()
            .build()
        billingClient?.startConnection(this)
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    private fun endConnection() {
        billingClient?.endConnection()
        lifecycle?.removeObserver(this)
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        billingResult.launchOnBillingResult {
            querySkuDetails()
            billingClient?.queryPurchasesAsync(BillingClient.SkuType.INAPP)?.purchasesList
                ?.handlePurchases()
        }
    }

    override fun onBillingServiceDisconnected() {
        if (lifecycle?.currentState == Lifecycle.State.CREATED) {
            billingClient?.startConnection(this)
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        billingResult.launchOnBillingResult {
            purchases?.handlePurchases()
        }
    }

    private suspend fun querySkuDetails() {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(BuildConfig.PRODUCT_IDS.toList())
            .setType(BillingClient.SkuType.INAPP)
            .build()
        val skuDetailsResult = billingClient?.querySkuDetails(params)
        if (skuDetailsResult?.billingResult?.isOkResponse() == true) {
            skuDetailsResult.skuDetailsList?.let { productRepository.setupProducts(it) }
        }
    }

    private suspend fun List<Purchase>.handlePurchases() = forEach { purchase ->
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
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

    private fun BillingResult.launchOnBillingResult(onOkResult: suspend () -> Unit) {
        launch {
            if (isOkResponse()) {
                onOkResult.invoke()
            } else {
                when (responseCode) {
                    BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> "Service timeout"
                    BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> "Feature not supported"
                    BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> "Service disconnected"
                    BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> "Service unavailable"
                    BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> "Billing unavailable"
                    BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> "Item unavailable"
                    BillingClient.BillingResponseCode.DEVELOPER_ERROR -> "Developer error"
                    BillingClient.BillingResponseCode.ERROR -> "Error"
                    else -> null
                }?.let { _errors.emit(it) }
            }
        }
    }

    private fun BillingResult.isOkResponse() = responseCode == BillingClient.BillingResponseCode.OK
}
