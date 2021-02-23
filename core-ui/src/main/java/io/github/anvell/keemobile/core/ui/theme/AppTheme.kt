package io.github.anvell.keemobile.core.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
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

    Providers(LocalAppColors provides appColors) {
        MaterialTheme(
            colors = if (isLight) ThemeElements.colorsLight else ThemeElements.colors,
            typography = ThemeElements.typography,
            shapes = ThemeElements.shapes,
            content = content
        )
    }
}

object AppTheme {
    @Composable
    val colors: AppColors
        get() = LocalAppColors.current
}
