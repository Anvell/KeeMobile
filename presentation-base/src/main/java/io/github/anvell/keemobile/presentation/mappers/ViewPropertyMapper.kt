package io.github.anvell.keemobile.presentation.mappers

import androidx.dynamicanimation.animation.DynamicAnimation
import io.github.anvell.keemobile.presentation.R

object ViewPropertyMapper {

    fun toId(property: DynamicAnimation.ViewProperty) = when (property) {
        DynamicAnimation.TRANSLATION_X -> R.id.view_translation_x
        DynamicAnimation.TRANSLATION_Y -> R.id.view_translation_y
        DynamicAnimation.TRANSLATION_Z -> R.id.view_translation_z
        DynamicAnimation.SCALE_X -> R.id.view_scale_x
        DynamicAnimation.SCALE_Y -> R.id.view_scale_y
        DynamicAnimation.ROTATION -> R.id.view_rotation
        DynamicAnimation.ROTATION_X -> R.id.view_rotation_x
        DynamicAnimation.ROTATION_Y -> R.id.view_rotation_y
        DynamicAnimation.X -> R.id.view_x
        DynamicAnimation.Y -> R.id.view_y
        DynamicAnimation.Z -> R.id.view_z
        DynamicAnimation.ALPHA -> R.id.view_alpha
        DynamicAnimation.SCROLL_X -> R.id.view_scroll_x
        DynamicAnimation.SCROLL_Y -> R.id.view_scroll_y
        else -> throw IllegalArgumentException("Unknown ViewProperty: $property")
    }
}
