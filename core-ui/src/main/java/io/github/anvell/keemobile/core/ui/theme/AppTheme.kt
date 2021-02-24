package io.github.anvell.keemobile.core.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember

@Composable
fun AppTheme(
    isLight: Boolean = false,
    content: @Composable () -> Unit
) {
    val appColors = remember {
        AppColors(
            when {
                isLight -> ThemeElements.filterColorsLight
                else -> ThemeElements.filterColors
            }
        )
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colors = if (isLight) ThemeElements.colorsLight else ThemeElements.colors,
            typography = ThemeElements.typography,
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
