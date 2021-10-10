package my.handbook.usecase

import android.app.Activity
import simple.billing.core.BillingHandler
import simple.billing.data.db.Product
import javax.inject.Inject

class PurchaseProductUseCase @Inject constructor(
    private val billingHandler: BillingHandler,
) {

    fun execute(activity: Activity?, product: Product) {
        activity?.let {
            billingHandler.purchaseProduct(it, product.originalJson)
        }
    }
}
