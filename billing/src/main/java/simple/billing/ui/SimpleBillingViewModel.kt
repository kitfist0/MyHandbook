package simple.billing.ui

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import simple.billing.core.BillingHandler
import simple.billing.data.db.Product

class SimpleBillingViewModel(application: Application) : AndroidViewModel(application) {

    private val billingHandler = BillingHandler.getInstance(getApplication())

    val products: LiveData<List<Product>> = billingHandler.products.asLiveData()

    fun purchaseProduct(activity: Activity, originalJson: String) =
        billingHandler.purchaseProduct(activity, originalJson)
}
