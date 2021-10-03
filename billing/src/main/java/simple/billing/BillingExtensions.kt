package simple.billing

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import simple.billing.core.BillingGlobal
import simple.billing.core.BillingHandler
import simple.billing.data.db.Product

inline fun ifNothingIsPurchased(block: () -> Unit) {
    if (BillingGlobal.HAS_PURCHASED_PRODUCTS == false) {
        block.invoke()
    }
}

fun AppCompatActivity.attachBillingToActivityLifecycle() {
    BillingHandler.getInstance(this).initBilling(lifecycle)
}

fun Fragment.attachBillingToFragmentLifecycle() {
    BillingHandler.getInstance(requireContext()).initBilling(lifecycle)
}

fun Product.isPurchased() = purchaseToken.isNotEmpty()
