package io.github.anvell.keemobile.presentation.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import io.github.anvell.keemobile.R
import io.github.anvell.keemobile.common.extensions.toPxScaled
import io.github.anvell.keemobile.common.state.StateProperty
import io.github.anvell.keemobile.common.state.StateHandlerView

interface MaskedText {
    fun isMasked(): Boolean
    fun setMasked(value: Boolean)
}

class PasswordTextView(context: Context, attrs: AttributeSet?)
    : AppCompatTextView(context, attrs), MaskedText, StateHandlerView {

    override val stateBundle = Bundle()

    private val dotsRadius = DOTS_RADIUS.toPxScaled()

    private val maskedPaint = Paint().apply {
        isAntiAlias = true
        color = currentTextColor
        style = Paint.Style.FILL
    }

    private var maskedText: Boolean by StateProperty(true)
    private var hideLength: Boolean by StateProperty(true)

    init {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.PasswordTextView, 0, 0)

        try {
            hideLength = attributes.getBoolean(R.styleable.PasswordTextView_hideLength, true)
            maskedText = attributes.getBoolean(R.styleable.PasswordTextView_isMasked, true)
        } finally {
            attributes.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (isMasked()) {
            val minimumWidth = paddingLeft + paddingRight + suggestedMinimumWidth
            val dotsWidth = (dotsRadius * 2 + DOTS_MARGIN_DP.toPxScaled()).toInt() * getDotsCount()
            val width = View.resolveSizeAndState(minimumWidth + dotsWidth, widthMeasureSpec, 1)
            setMeasuredDimension(width, measuredHeight)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (isMasked()) {
            canvas?.run {
                var shift = 0f
                for (i in 1..getDotsCount()) {
                    drawCircle(dotsRadius + shift, height / 2f, dotsRadius, maskedPaint)
                    shift = (dotsRadius * 2) * i + DOTS_MARGIN_DP.toPxScaled() * i
                }
            }
        } else {
            super.onDraw(canvas)
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        return saveState(super.onSaveInstanceState())
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(restoreState(state))
    }

    override fun isMasked() = maskedText

    override fun setMasked(value: Boolean) {
        maskedText = value
        requestLayout()
        invalidate()
    }

    fun isLengthHidden() = hideLength

    fun setLengthHidden(value: Boolean) {
        hideLength = value
        requestLayout()
        invalidate()
    }

    private fun getDotsCount() = if (hideLength) {
        DOTS_QUANTITY
    } else {
        text.length
    }

    companion object {
        private const val DOTS_RADIUS = 3f
        private const val DOTS_QUANTITY = 6
        private const val DOTS_MARGIN_DP = 2f
    }

}
