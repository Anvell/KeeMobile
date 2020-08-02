package io.github.anvell.keemobile.presentation.theme

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.BuildCompat

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
                if (BuildCompat.isAtLeastQ()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
        }
    }
}
