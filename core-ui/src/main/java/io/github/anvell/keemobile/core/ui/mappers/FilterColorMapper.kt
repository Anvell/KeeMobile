package io.github.anvell.keemobile.core.ui.mappers

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import kotlin.math.abs

class FilterColorMapper {

    val defaultColor: Int
    val targetColors: List<FloatArray>

    constructor(defaultColor: Int, targetColors: IntArray) {
        this.defaultColor = defaultColor
        this.targetColors = targetColors.map(this::rgbToHsl)
    }

    fun map(hexColor: String?): Int = if (hexColor != null) {
        try {
            map(Color.parseColor(hexColor))
        } catch (e: NumberFormatException) {
            defaultColor
        }
    } else {
        defaultColor
    }

    fun map(@ColorInt inputColor: Int): Int {
        val inputColorHsl = rgbToHsl(inputColor)
        var outColor = targetColors.first()

        for (color in targetColors) {
            val diff = abs(color[H] - inputColorHsl[H])
            val lastDiff = abs(outColor[H] - inputColorHsl[H])

            if (diff < lastDiff) {
                outColor = color
            }
        }

        return ColorUtils.HSLToColor(outColor)
    }

    private fun rgbToHsl(@ColorInt inputColor: Int): FloatArray {
        val inputColorHsl = FloatArray(3)
        ColorUtils.colorToHSL(inputColor, inputColorHsl)
        return inputColorHsl
    }

    companion object {
        private const val H = 0
        private const val S = 1
        private const val L = 2
    }

}
