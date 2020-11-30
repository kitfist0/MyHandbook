package my.handbook.util

import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import androidx.annotation.ColorInt
import my.handbook.R

fun SharedPreferences.dbRecreationRequired(dbFileName: String): Boolean =
    if (contains(dbFileName)) {
        false
    } else {
        edit().putBoolean(dbFileName, true).apply()
        true
    }

@ColorInt
fun Resources.getSectionColor(section: Int?) = section?.sectionNumberToIndex()
    ?.let {
        val colors = getStringArray(R.array.section_colors)
        Color.parseColor(colors[it % colors.size])
    }
    ?: Color.TRANSPARENT

fun Resources.getSectionNameStringRes(section: Int?): String = section?.sectionNumberToIndex()
    ?.let {
        val names = getStringArray(R.array.section_names)
        if (it >= names.size) {
            "Section#$section"
        } else {
            names[it]
        }
    }
    ?: ""

private fun Int.sectionNumberToIndex() = this - 1
