package my.handbook.billing.ui

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import my.handbook.billing.core.BillingHandler
import my.handbook.billing.core.BillingUseCases
import my.handbook.billing.data.db.Product

abstract class BillingViewModel constructor(
    private val billingHandler: BillingHandler
) : ViewModel(), BillingUseCases {

    override val products: LiveData<List<Product>> = billingHandler.products

    override fun initBilling(lifecycle: Lifecycle) = billingHandler.initBilling(lifecycle)

    override fun purchaseProduct(activity: Activity, originalJson: String) =
        billingHandler.purchaseProduct(activity, originalJson)
}
