package my.handbook.util

import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import my.handbook.BuildConfig
import my.handbook.R

fun SharedPreferences.dbRecreationRequired(): Boolean =
    if (contains(BuildConfig.ACTUAL_DB_FILE_NAME)) {
        false
    } else {
        edit().putBoolean(BuildConfig.ACTUAL_DB_FILE_NAME, true).apply()
        true
    }

fun Resources.getSectionColorRes(section: Int?) = section?.sectionNumberToIndex()
    ?.let {
        val colors = getStringArray(R.array.section_colors)
        Color.parseColor(colors[it % colors.size])
    }
    ?: Color.TRANSPARENT

fun Resources.getSectionNameStringRes(section: Int?): String = section?.sectionNumberToIndex()
    ?.let {
        val names = getStringArray(R.array.section_names)
        if (it >= names.size) {
            getString(R.string.default_section_name).format(section)
        } else {
            names[it]
        }
    }
    ?: ""

private fun Int.sectionNumberToIndex() = this - 1

