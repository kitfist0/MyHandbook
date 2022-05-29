package my.handbook.ui.drawer

import android.view.View
import androidx.annotation.FloatRange
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.shape.MaterialShapeDrawable
import my.handbook.util.normalize

/**
 * An action to be performed when a bottom sheet's slide offset is changed.
 */
interface OnSlideAction {
    /**
     * Called when the bottom sheet's [slideOffset] is changed. [slideOffset] will always be a
     * value between -1.0 and 1.0. -1.0 is equal to [BottomSheetBehavior.STATE_HIDDEN], 0.0
     * is equal to [BottomSheetBehavior.STATE_HALF_EXPANDED] and 1.0 is equal to
     * [BottomSheetBehavior.STATE_EXPANDED].
     */
    fun onSlide(
        sheet: View,
        @FloatRange(
            from = -1.0,
            fromInclusive = true,
            to = 1.0,
            toInclusive = true
        ) slideOffset: Float
    )
}

/**
 * A slide action which rotates a view counterclockwise by 180 degrees between the hidden state
 * and the half expanded state.
 */
class HalfClockwiseRotateSlideAction(
    private val view: View,
) : OnSlideAction {
    override fun onSlide(sheet: View, slideOffset: Float) {
        view.rotation = slideOffset.normalize(
            -1F,
            0F,
            0F,
            180F
        )
    }
}

/**
 * A slide action which acts on the nav drawer between the half expanded state and
 * expanded state by:
 * - Removing the foreground sheets rounded corners/edge treatment
 */
class ForegroundSheetTransformSlideAction(
    private val foregroundShapeDrawable: MaterialShapeDrawable,
) : OnSlideAction {
    override fun onSlide(sheet: View, slideOffset: Float) {
        val progress = slideOffset.normalize(0F, 0.25F, 1F, 0F)
        foregroundShapeDrawable.interpolation = progress
    }
}

/**
 * Change the alpha of [view] when a bottom sheet is slid.
 *
 * @param reverse Setting reverse to true will cause the view's alpha to approach 0.0 as the sheet
 *  slides up. The default behavior, false, causes the view's alpha to approach 1.0 as the sheet
 *  slides up.
 */
class AlphaSlideAction(
    private val view: View,
    private val reverse: Boolean = false,
) : OnSlideAction {
    override fun onSlide(sheet: View, slideOffset: Float) {
        val alpha = slideOffset.normalize(-1F, 0F, 0F, 1F)
        view.alpha = if (!reverse) alpha else 1F - alpha
    }
}
