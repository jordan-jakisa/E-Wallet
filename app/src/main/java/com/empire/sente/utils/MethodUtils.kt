package com.empire.sente.utils

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.core.content.ContextCompat
import androidx.core.text.color
import com.empire.sente.R

object MethodUtils {

    fun stringBuilder(t1: String, t2: String, context: Context): SpannableStringBuilder {
        return SpannableStringBuilder().let {
                it.append("$t1 ")
                it.color(
                    ContextCompat.getColor(
                        context,
                        R.color.md_theme_light_primary
                    )
                ) { append(" $t2") }
            }
        }
}