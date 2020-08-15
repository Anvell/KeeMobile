package io.github.anvell.keemobile.core.ui.theme

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate

object DayNightSwitcher {
    const val SYSTEM = 0
    const val DAY = 1
    const val NIGHT = 2

    fun apply(mode: Int) {
        when (mode) {
            DAY -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            NIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            SYSTEM -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }
    }
}
