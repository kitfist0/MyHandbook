package my.handbook.billing.util

import android.widget.TextView
import androidx.annotation.MainThread
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.*
import my.handbook.billing.R
import my.handbook.billing.data.db.Product
import my.handbook.billing.ui.BillingViewModel
import my.handbook.common.bindDrawables

@MainThread
inline fun <reified VM : ViewModel> Fragment.billingViewModels(
    noinline ownerProducer: () -> ViewModelStoreOwner = { this },
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
) = createViewModelLazy(VM::class, { ownerProducer().viewModelStore }, factoryProducer)
    .also { lazyModel ->
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(value = Lifecycle.Event.ON_CREATE)
            fun initBilling() {
                (lazyModel.value as BillingViewModel).initBilling(lifecycle)
                lifecycle.removeObserver(this)
            }
        })
    }

fun Product.isPurchased() = purchaseToken.isNotEmpty()

@BindingAdapter("productIcon")
fun TextView.bindProductIcon(product: Product?) = bindDrawables(
    drawableStart = if (product?.isPurchased() == true) {
        R.drawable.ic_product_purchased
    } else {
        R.drawable.ic_product_default
    }
)
