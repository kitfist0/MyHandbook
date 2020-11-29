package my.handbook.billing.core

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import my.handbook.billing.data.db.Product

interface BillingUseCases {

    val products: LiveData<List<Product>>

    fun initBilling(lifecycle: Lifecycle)

    fun purchaseProduct(activity: Activity, originalJson: String)
}
