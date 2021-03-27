package io.github.anvell.keemobile.core.ui.mappers

import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import kotlin.math.abs
import android.graphics.Color as NativeColor

object ComposeFilterColorMapper {
    private const val Hue = 0

    fun map(
        hexColor: String,
        targetColors: List<Color>
    ): Color? = try {
        map(NativeColor.parseColor(hexColor), targetColors)
    } catch (e: NumberFormatException) {
        null
    }

    fun map(
        @ColorInt inputColor: Int,
        targetColors: List<Color>
    ): Color {
        val targetColorsHsl = targetColors.map { rgbToHsl(it.toArgb()) }
        val inputColorHsl = rgbToHsl(inputColor)
        var outColor = targetColorsHsl.first()

        for (color in targetColorsHsl) {
            val diff = abs(color[Hue] - inputColorHsl[Hue])
            val lastDiff = abs(outColor[Hue] - inputColorHsl[Hue])

            if (diff < lastDiff) {
                outColor = color
            }
        }

        return Color(ColorUtils.HSLToColor(outColor))
    }

    private fun rgbToHsl(@ColorInt inputColor: Int): FloatArray {
        val inputColorHsl = FloatArray(3)
        ColorUtils.colorToHSL(inputColor, inputColorHsl)
        return inputColorHsl
    }
}
