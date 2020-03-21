package io.github.anvell.keemobile.presentation.widgets

import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import io.github.anvell.keemobile.R

class AnimatedImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {

    private val animDrawable: Drawable?
    private val animReversedDrawable: Drawable?
    private var isAtStart = true

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
        return SavedState(super.onSaveInstanceState()).apply {
            isAtStart = this@AnimatedImageView.isAtStart
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        isAtStart = savedState.isAtStart
        setPosition(isAtStart)
    }

    internal class SavedState : BaseSavedState {
        var isAtStart = true

        constructor(superState: Parcelable?) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            isAtStart = parcel.readBoolean()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeBoolean(isAtStart)
        }

        companion object {

            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(parcel: Parcel): SavedState {
                    return SavedState(parcel)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}
