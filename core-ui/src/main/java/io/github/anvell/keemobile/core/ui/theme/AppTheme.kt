package io.github.anvell.keemobile.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val (colors, appColors) = remember(isDarkTheme) {
        if (isDarkTheme) {
            ThemeElements.colorsDark to AppColors(ThemeElements.filterColorsDark)
        } else {
            ThemeElements.colorsLight to AppColors(ThemeElements.filterColorsLight)
        }
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colors = colors,
            typography = ThemeElements.typography(colors),
            shapes = ThemeElements.shapes,
            content = content
        )
    }
}

object AppTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
}
