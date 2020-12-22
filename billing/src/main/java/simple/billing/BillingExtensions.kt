package simple.billing

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.BindingAdapter
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

@BindingAdapter("productIcon")
fun TextView.bindProductIcon(product: Product?) {
    val drawableRes = when (product?.isPurchased()) {
        true -> R.drawable.ic_product_purchased
        else -> R.drawable.ic_product_default
    }
    setCompoundDrawablesWithIntrinsicBounds(
        AppCompatResources.getDrawable(context, drawableRes),
        null, null, null
    )
}
