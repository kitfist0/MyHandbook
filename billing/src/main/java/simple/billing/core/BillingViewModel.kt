package simple.billing.core

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import simple.billing.data.db.Product

open class BillingViewModel(context: Context) : ViewModel(), BillingUseCases {

    private val billingHandler = BillingHandler.getInstance(context)

    override val products: LiveData<List<Product>> = billingHandler.products

    override fun purchaseProduct(activity: Activity, originalJson: String) =
        billingHandler.purchaseProduct(activity, originalJson)
}
