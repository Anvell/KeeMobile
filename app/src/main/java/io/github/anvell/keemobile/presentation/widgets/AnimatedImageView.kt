package io.github.anvell.keemobile.presentation.widgets

import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import io.github.anvell.keemobile.R

class AnimatedImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {

    private val animDrawable: Drawable?
    private val animReversedDrawable: Drawable?

    init {
        animDrawable = getDrawable()
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.AnimatedImageView, 0, 0)

        try {
            animReversedDrawable = attributes.getDrawable(R.styleable.AnimatedImageView_srcReversed)
        } finally {
            attributes.recycle()
        }
    }

    fun play() {
        setImageDrawable(animDrawable)

        if (animDrawable != null && animDrawable is Animatable) {
            with(animDrawable as Animatable) {
                if (!isRunning) {
                    start()
                }
            }
        }
    }

    fun playReverse() {
        setImageDrawable(animReversedDrawable)

        if (animReversedDrawable != null && animReversedDrawable is Animatable) {
            with(animReversedDrawable as Animatable) {
                if (!isRunning) {
                    start()
                }
            }
        }
    }
}
