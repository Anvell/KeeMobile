package io.github.anvell.keemobile.core.ui.extensions

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import io.github.anvell.keemobile.core.ui.mappers.ViewPropertyMapper

fun View.clipToCornerRadius(radius: Float) {
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            outline?.setRoundRect(0, 0, view!!.width, view.height, radius)
        }
    }
    clipToOutline = true
}

fun View.springAnimation(
    property: DynamicAnimation.ViewProperty,
    stiffness: Float = SpringForce.STIFFNESS_LOW,
    dampingRatio: Float = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY,
    velocity: Float = 0f
): SpringAnimation {
    val key = ViewPropertyMapper.toId(property)
    var animation = getTag(key) as? SpringAnimation?

    if (animation == null) {
        animation = SpringAnimation(this, property)
        setTag(key, animation)
    }

    with(animation) {
        spring = (animation.spring ?: SpringForce()).apply {
            this.stiffness = stiffness
            this.dampingRatio = dampingRatio
        }
        setStartVelocity(velocity)
    }

    return animation
}
