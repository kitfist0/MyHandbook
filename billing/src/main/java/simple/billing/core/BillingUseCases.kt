package simple.billing.core

import android.app.Activity
import androidx.lifecycle.LiveData
import simple.billing.data.db.Product

interface BillingUseCases {

    val products: LiveData<List<Product>>

    fun purchaseProduct(activity: Activity, originalJson: String)
}
