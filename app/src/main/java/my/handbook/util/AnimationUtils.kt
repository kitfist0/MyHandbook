package my.handbook.util

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.google.android.material.animation.ArgbEvaluatorCompat

/**
 * Linearly interpolate between two values
 */
fun lerp(
    startValue: Float,
    endValue: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float,
): Float {
    return startValue + fraction * (endValue - startValue)
}

/**
 * Linearly interpolate between two colors when the fraction is in a given range.
 */
@ColorInt
fun lerpArgb(
    @ColorInt startColor: Int,
    @ColorInt endColor: Int,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = false) startFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = false, to = 1.0, toInclusive = true) endFraction: Float,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true) fraction: Float,
): Int {
    return when {
        fraction < startFraction -> startColor
        fraction > endFraction -> endColor
        else -> ArgbEvaluatorCompat.getInstance().evaluate(
            (fraction - startFraction) / (endFraction - startFraction),
            startColor,
            endColor
        )
    }
}

/**
 * Coerce the receiving Float between inputMin and inputMax and linearly interpolate to the
 * outputMin to outputMax scale. This function is able to handle ranges which span negative and
 * positive numbers.
 *
 * This differs from [lerp] as the input values are not required to be between 0 and 1.
 */
fun Float.normalize(
    inputMin: Float,
    inputMax: Float,
    outputMin: Float,
    outputMax: Float,
): Float {
    return when {
        this < inputMin -> outputMin
        this > inputMax -> outputMax
        else -> outputMin * (1 - (this - inputMin) / (inputMax - inputMin)) +
                outputMax * ((this - inputMin) / (inputMax - inputMin))
    }
}
