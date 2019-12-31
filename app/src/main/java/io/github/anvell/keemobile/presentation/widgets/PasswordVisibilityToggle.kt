package io.github.anvell.keemobile.presentation.widgets

import android.content.Context
import android.graphics.Typeface
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatToggleButton
import io.github.anvell.keemobile.R

class PasswordVisibilityToggle(context: Context, attrs: AttributeSet) : AppCompatToggleButton(context, attrs) {

    private val boundViewId: Int

    init {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.PasswordVisibilityToggle, 0, 0)

        try {
            boundViewId = attributes.getResourceId(R.styleable.PasswordVisibilityToggle_boundView, -1)
        } finally {
            attributes.recycle()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (boundViewId > 0) {
            val view = (parent as View).findViewById<View>(boundViewId)

            setOnCheckedChangeListener { _, isChecked ->
                (view as? MaskedText)?.run { setMasked(!isChecked) }
            }
        }
    }
}
