package io.github.anvell.keemobile.presentation.widgets

import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import io.github.anvell.keemobile.presentation.R
import io.github.anvell.keemobile.presentation.state.ViewInstanceStateHandler

class AnimatedImageView(context: Context, attrs: AttributeSet?) :
    AppCompatImageView(context, attrs), ViewInstanceStateHandler {

    override val stateBundle = Bundle()

    private var isAtStart by stateProperty(true)

    private val animDrawable: Drawable?
    private val animReversedDrawable: Drawable?

    init {
        animDrawable = drawable
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.AnimatedImageView, 0, 0)

        try {
            animReversedDrawable = attributes.getDrawable(R.styleable.AnimatedImageView_srcReversed)
        } finally {
            attributes.recycle()
        }
    }

    fun rewindToStart() {
        isAtStart = true
        setImageDrawable(animDrawable)
    }

    fun rewindToEnd() {
        isAtStart = false
        setImageDrawable(animReversedDrawable)
    }

    fun play() {
        rewindToStart()

        if (animDrawable != null && animDrawable is Animatable) {
            with(animDrawable as Animatable) {
                if (!isRunning) {
                    isAtStart = false
                    start()
                }
            }
        }
    }

    fun playReverse() {
        rewindToEnd()

        if (animReversedDrawable != null && animReversedDrawable is Animatable) {
            with(animReversedDrawable as Animatable) {
                if (!isRunning) {
                    isAtStart = true
                    start()
                }
            }
        }
    }

    private fun setPosition(isAtStart: Boolean) = when (isAtStart) {
        true -> rewindToStart()
        else -> rewindToEnd()
    }

    override fun onSaveInstanceState(): Parcelable? {
        return saveState(super.onSaveInstanceState())
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(restoreState(state))
        setPosition(isAtStart)
    }

}
