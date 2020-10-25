package my.handbook.util

import android.content.SharedPreferences
import my.handbook.BuildConfig
import my.handbook.R

fun SharedPreferences.dbRecreationRequired(): Boolean =
    if (contains(BuildConfig.ACTUAL_DB_FILE_NAME)) {
        false
    } else {
        edit().putBoolean(BuildConfig.ACTUAL_DB_FILE_NAME, true).apply()
        true
    }

fun String?.getSectionColorRes(): Int = when(this) {
    "ag" -> R.color.section_ag
    "ma" -> R.color.section_ma
    "pt" -> R.color.section_pt
    else -> android.R.color.transparent
}

fun String?.getSectionStringRes(): Int = when(this) {
    "ag" -> R.string.section_ag
    "ma" -> R.string.section_ma
    "pt" -> R.string.section_pt
    else -> R.string.section_other
}
