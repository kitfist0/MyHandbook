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

    val errors: LiveData<String> = billingHandler.errors.asLiveData()

    fun onProductClicked(activity: Activity?, product: Product) =
        activity?.let { billingHandler.purchaseProduct(it, product.originalJson) }
}
