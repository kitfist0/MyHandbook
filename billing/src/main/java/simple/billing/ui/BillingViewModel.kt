package simple.billing.ui

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import simple.billing.core.BillingHandler
import simple.billing.core.BillingUseCases
import simple.billing.data.db.Product

class BillingViewModel constructor(context: Context) : ViewModel(), BillingUseCases {

    private val billingHandler = BillingHandler.getInstance(context)

    override val products: LiveData<List<Product>> = billingHandler.products

    override fun purchaseProduct(activity: Activity, originalJson: String) =
        billingHandler.purchaseProduct(activity, originalJson)
}
