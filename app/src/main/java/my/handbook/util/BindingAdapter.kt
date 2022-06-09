package my.handbook.util

import android.graphics.drawable.Animatable
import android.os.Build
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.BindingAdapter
import my.handbook.R
import my.handbook.data.entity.Section

@BindingAdapter("sectionText")
fun TextView.bindSectionText(section: Int?) {
    text = context.resources.getSectionNameStringRes(section)
}

@BindingAdapter("sectionDrawable")
fun TextView.bindSectionDrawable(section: Section?) {
    if (section?.selected == true) {
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_round_dot)
        drawable?.setTint(context.resources.getSectionColor(section.id))
        setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    } else {
        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_twotone_dot, 0, 0, 0)
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
        WindowInsetsCompat.toWindowInsetsCompat(insets)
            .getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars())
            .apply {
                view.setPadding(
                    padding.left + if (applyLeft) left else 0,
                    padding.top + if (applyTop) top else 0,
                    padding.right + if (applyRight) right else 0,
                    padding.bottom + if (applyBottom) bottom else 0
                )
            }
    }
}

private fun View.doOnApplyWindowInsets(
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
    "drawableLeft",
    "drawableTop",
    "drawableEnd",
    "drawableRight",
    "drawableBottom",
    requireAll = false
)
fun TextView.bindDrawables(
    @DrawableRes drawableStart: Int? = null,
    @DrawableRes drawableLeft: Int? = null,
    @DrawableRes drawableTop: Int? = null,
    @DrawableRes drawableEnd: Int? = null,
    @DrawableRes drawableRight: Int? = null,
    @DrawableRes drawableBottom: Int? = null
) {
    setCompoundDrawablesWithIntrinsicBounds(
        context.getDrawableOrNull(drawableStart ?: drawableLeft),
        context.getDrawableOrNull(drawableTop),
        context.getDrawableOrNull(drawableEnd ?: drawableRight),
        context.getDrawableOrNull(drawableBottom)
    )
}

@BindingAdapter("goneIf")
fun View.bindGoneIf(gone: Boolean) {
    visibility = if (gone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("layoutFullscreen")
fun View.bindLayoutFullscreen(previousFullscreen: Boolean, fullscreen: Boolean) {
    if (previousFullscreen != fullscreen && fullscreen) {
        systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }
}
