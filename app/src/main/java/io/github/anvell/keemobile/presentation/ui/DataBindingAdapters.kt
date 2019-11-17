package io.github.anvell.keemobile.presentation.ui

import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter
import io.github.anvell.keemobile.R

object DataBindingAdapters {

    @JvmStatic
    @BindingAdapter("customSrc")
    fun setImageResource(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource)
    }

    @JvmStatic
    @BindingAdapter("customTint")
    fun setTint(imageView: ImageView, @ColorInt color: Int?) {
        color?.let {
            ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(it))
        }
    }
}
