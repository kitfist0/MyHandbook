package my.handbook.util

import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.os.Build
import android.text.Html
import android.view.View
import android.view.WindowInsets
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.databinding.BindingAdapter
import my.handbook.R
import my.handbook.data.local.model.Section

@BindingAdapter("sectionText")
fun TextView.bindSectionText(section: Int?) {
    text = resources.getSectionNameStringRes(section)
}

@BindingAdapter("sectionDrawable")
fun TextView.bindSectionDrawable(section: Section?) {
    if (section?.selected == true) {
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_round_dot)
        drawable?.setTint(resources.getSectionColor(section.id))
        setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    } else {
        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_twotone_dot, 0, 0, 0)
    }
}

@BindingAdapter("sectionTextColor")
fun TextView.bindSectionTextColor(section: Int?) {
    setTextColor(resources.getSectionColor(section))
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
    "paddingTopSystemWindowInsets",
    "paddingBottomSystemWindowInsets",
    requireAll = false
)
fun View.applySystemWindowInsetsPadding(
    previousApplyTop: Boolean,
    previousApplyBottom: Boolean,
    applyTop: Boolean,
    applyBottom: Boolean,
) {
    if (previousApplyTop == applyTop && previousApplyBottom == applyBottom) {
        return
    }
    doOnApplyWindowInsets { view, insets, initialPadding ->
        WindowInsetsCompat.toWindowInsetsCompat(insets)
            .getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars())
            .apply {
                view.setPadding(
                    initialPadding.left,
                    initialPadding.top + if (applyTop) top else 0,
                    initialPadding.right,
                    initialPadding.bottom + if (applyBottom) bottom else 0
                )
            }
    }
}

private fun View.doOnApplyWindowInsets(
    block: (View, WindowInsets, Rect) -> Unit
) {
    // Create a snapshot of the view's padding
    val initialPadding = Rect(paddingLeft, paddingTop, paddingRight, paddingBottom)
    // Set an actual OnApplyWindowInsetsListener which proxies to the given
    // lambda, also passing in the original padding states
    setOnApplyWindowInsetsListener { v, insets ->
        block(v, insets, initialPadding)
        // Always return the insets, so that children can also use them
        insets
    }
    // request some insets
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to request when we are
        addOnAttachStateChangeListener(
            object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    v.removeOnAttachStateChangeListener(this)
                    v.requestApplyInsets()
                }

                override fun onViewDetachedFromWindow(v: View) = Unit
            }
        )
    }
}

@BindingAdapter("coffeeIcon")
fun TextView.bindDrawerCoffeeItemIcon(isPurchased: Boolean) {
    val drawableResId = if (isPurchased) {
        R.drawable.ic_product_purchased
    } else {
        R.drawable.ic_product_default
    }
    setCompoundDrawablesWithIntrinsicBounds(
        AppCompatResources.getDrawable(context, drawableResId),
        null, null, null
    )
}

@BindingAdapter(
    "drawableStart",
    "drawableTop",
    "drawableEnd",
    "drawableBottom",
    requireAll = false
)
fun TextView.bindDrawables(
    @DrawableRes drawableStart: Int? = null,
    @DrawableRes drawableTop: Int? = null,
    @DrawableRes drawableEnd: Int? = null,
    @DrawableRes drawableBottom: Int? = null,
) {
    setCompoundDrawablesWithIntrinsicBounds(
        context.getDrawableOrNull(drawableStart),
        context.getDrawableOrNull(drawableTop),
        context.getDrawableOrNull(drawableEnd),
        context.getDrawableOrNull(drawableBottom)
    )
}

@BindingAdapter("goneIf")
fun View.bindGoneIf(gone: Boolean) {
    isGone = gone
}
