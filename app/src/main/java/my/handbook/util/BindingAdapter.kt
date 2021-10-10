package my.handbook.util

import android.graphics.drawable.Animatable
import android.os.Build
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import my.handbook.R
import my.handbook.data.entity.Section
import simple.billing.data.db.Product
import simple.billing.data.db.Product.Companion.getProductDrawable

@BindingAdapter("sectionText")
fun TextView.bindSectionText(section: Int?) {
    text = context.resources.getSectionNameStringRes(section)
}

@BindingAdapter("sectionDrawable")
fun TextView.bindSectionDrawable(section: Section?) {
    when (section?.selected) {
        true -> {
            val drawable = ContextCompat.getDrawable(context, R.drawable.ic_round_dot)
            drawable?.setTint(context.resources.getSectionColor(section.id))
            setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        }
        false -> {
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_twotone_dot, 0, 0, 0)
        }
    }
}

@BindingAdapter("sectionTextColor")
fun TextView.bindSectionTextColor(section: Int?) {
    setTextColor(context.resources.getSectionColor(section))
}

@Suppress("DEPRECATION")
@BindingAdapter("textSnippet")
fun TextView.bindTextSnippet(snippet: String) {
    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(snippet, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(snippet)
    }
}

@BindingAdapter("loadingAnimation")
fun EditText.bindLoadingAnimation(loading: Boolean) {
    val drawable = compoundDrawablesRelative[2]
    val anim = drawable as Animatable
    if (loading) {
        anim.start()
        drawable.alpha = 255
    } else {
        anim.stop()
        drawable.alpha = 0
    }
}

@BindingAdapter(
    "paddingLeftSystemWindowInsets",
    "paddingTopSystemWindowInsets",
    "paddingRightSystemWindowInsets",
    "paddingBottomSystemWindowInsets",
    requireAll = false
)
fun View.applySystemWindowInsetsPadding(
    previousApplyLeft: Boolean,
    previousApplyTop: Boolean,
    previousApplyRight: Boolean,
    previousApplyBottom: Boolean,
    applyLeft: Boolean,
    applyTop: Boolean,
    applyRight: Boolean,
    applyBottom: Boolean
) {
    if (previousApplyLeft == applyLeft &&
        previousApplyTop == applyTop &&
        previousApplyRight == applyRight &&
        previousApplyBottom == applyBottom
    ) {
        return
    }

    doOnApplyWindowInsets { view, insets, padding, _, _ ->
        val left = if (applyLeft) insets.systemWindowInsetLeft else 0
        val top = if (applyTop) insets.systemWindowInsetTop else 0
        val right = if (applyRight) insets.systemWindowInsetRight else 0
        val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

        view.setPadding(
            padding.left + left,
            padding.top + top,
            padding.right + right,
            padding.bottom + bottom
        )
    }
}

fun View.doOnApplyWindowInsets(
    block: (View, WindowInsets, InitialPadding, InitialMargin, Int) -> Unit
) {
    // Create a snapshot of the view's padding & margin states
    val initialPadding = recordInitialPaddingForView(this)
    val initialMargin = recordInitialMarginForView(this)
    val initialHeight = recordInitialHeightForView(this)
    // Set an actual OnApplyWindowInsetsListener which proxies to the given
    // lambda, also passing in the original padding & margin states
    setOnApplyWindowInsetsListener { v, insets ->
        block(v, insets, initialPadding, initialMargin, initialHeight)
        // Always return the insets, so that children can also use them
        insets
    }
    // request some insets
    requestApplyInsetsWhenAttached()
}

class InitialPadding(val left: Int, val top: Int, val right: Int, val bottom: Int)

class InitialMargin(val left: Int, val top: Int, val right: Int, val bottom: Int)

private fun recordInitialPaddingForView(view: View) = InitialPadding(
    view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom
)

private fun recordInitialMarginForView(view: View): InitialMargin {
    val lp = view.layoutParams as? ViewGroup.MarginLayoutParams
        ?: throw IllegalArgumentException("Invalid view layout params")
    return InitialMargin(lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin)
}

private fun recordInitialHeightForView(view: View): Int {
    return view.layoutParams.height
}

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to
        // request when we are
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

@BindingAdapter("productIcon")
fun TextView.bindDrawerProductItemIcon(product: Product?) {
    product?.let {
        setCompoundDrawablesWithIntrinsicBounds(
            AppCompatResources.getDrawable(context, it.getProductDrawable()),
            null, null, null
        )
    }
}
