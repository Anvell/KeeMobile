package io.github.anvell.keemobile.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.Shapes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

internal object ThemeElements {
    val colorsDark = Colors(
        primary = ColorPalette.teal_600,
        primaryVariant = ColorPalette.teal_900,
        secondary = ColorPalette.pink_600,
        secondaryVariant = ColorPalette.red,
        background = ColorPalette.blue_grey_800,
        surface = ColorPalette.blue_grey_700,
        error = ColorPalette.pink_400,
        onPrimary = ColorPalette.grey_200,
        onSecondary = ColorPalette.white,
        onBackground = ColorPalette.grey_300,
        onSurface = ColorPalette.blue_grey_300,
        onError = ColorPalette.white,
        isLight = false
    )

    val colorsLight = Colors(
        primary = ColorPalette.green_400,
        primaryVariant = ColorPalette.teal_900,
        secondary = ColorPalette.pink_600,
        secondaryVariant = ColorPalette.red,
        background = ColorPalette.white,
        surface = ColorPalette.grey_200,
        error = ColorPalette.pink_400,
        onPrimary = ColorPalette.white,
        onSecondary = ColorPalette.white,
        onBackground = ColorPalette.grey_800,
        onSurface = ColorPalette.grey_800,
        onError = ColorPalette.white,
        isLight = true
    )

    val filterColorsDark = listOf(
        Color(0xFFFFF176),
        Color(0xFF81C784),
        Color(0xFFE57373),
        Color(0xFFFF8A65),
        Color(0xFF7986CB),
        Color(0xFF9575CD),
    )

    val filterColorsLight = listOf(
        Color(0xFFFFCA28),
        Color(0xFF66BB6A),
        Color(0xFFEF5350),
        Color(0xFFFF7043),
        Color(0xFF5C6BC0),
        Color(0xFF7E57C2),
    )

    val shapes = Shapes(
        small = RoundedCornerShape(10.dp),
        medium = RoundedCornerShape(10.dp),
        large = RoundedCornerShape(12.dp)
    )

    fun typography(colors: Colors) = createTypography(colors)
}
