package io.github.anvell.keemobile.core.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.github.anvell.keemobile.core.ui.R

private val DefaultFontFamily = FontFamily(
    Font(R.font.firasans_regular),
    Font(R.font.firasans_regular_italic, style = FontStyle.Italic),
    Font(R.font.firasans_light, FontWeight.Light),
    Font(R.font.firasans_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.firasans_medium, FontWeight.Medium),
    Font(R.font.firasans_medium_italic, FontWeight.Medium, FontStyle.Italic),
)

internal fun typography(colors: Colors) = Typography(
    defaultFontFamily = DefaultFontFamily,
    subtitle1 = TextStyle(
        color = colors.onSecondary,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        letterSpacing = 0.sp
    ),
    body1 = TextStyle(
        color = colors.onBackground,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.sp,
        lineHeight = 1.5.em
    ),
    body2 = TextStyle(
        color = colors.onBackground,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.sp,
        lineHeight = 1.5.em
    ),
)
