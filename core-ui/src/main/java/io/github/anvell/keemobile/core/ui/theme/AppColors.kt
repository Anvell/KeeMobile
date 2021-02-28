package io.github.anvell.keemobile.core.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

class AppColors(
    val filterColors: List<Color>
)

val LocalAppColors = staticCompositionLocalOf<AppColors> {
    error("AppColors provider is required.")
}
