package io.github.anvell.keemobile.presentation.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import io.github.anvell.keemobile.R
import io.github.anvell.keemobile.common.extensions.toPxScaled

interface MaskedText {

    fun isMasked(): Boolean

    fun setMasked(hide: Boolean)
}

class PasswordTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs), MaskedText {

    private val dotsRadius = DOTS_RADIUS.toPxScaled()

    private val maskedPaint = Paint().apply {
        isAntiAlias = true
        color = currentTextColor
        style = Paint.Style.FILL
    }

    private val hideLength: Boolean

    init {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.PasswordTextView, 0, 0)

        try {
            hideLength = attributes.getBoolean(R.styleable.PasswordTextView_hideLength, false)
        } finally {
            attributes.recycle()
        }

        setMasked(true)
    }

    override fun isMasked() = inputType == MASKED

    override fun setMasked(hide: Boolean) {
        val currentTypeface = typeface
        inputType = if (hide) MASKED else UNMASKED
        typeface = currentTypeface
    }

    override fun onDraw(canvas: Canvas?) {
        if (inputType != MASKED) {
            super.onDraw(canvas)
        } else {
            canvas?.run {
                var shift = 0f
                for (i in 1..getDotsCount()) {
                    drawCircle(dotsRadius + shift, height / 2f, dotsRadius, maskedPaint)
                    shift = (dotsRadius * 2) * i + DOTS_MARGIN_DP.toPxScaled() * i
                }
            }
        }
    }

    private fun getDotsCount() = if (hideLength) {
        DOTS_QUANTITY
    } else {
        text.length
    }

    companion object {
        private const val MASKED = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        private const val UNMASKED = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

        private const val DOTS_RADIUS = 3f
        private const val DOTS_QUANTITY = 6
        private const val DOTS_MARGIN_DP = 2f
    }

}
