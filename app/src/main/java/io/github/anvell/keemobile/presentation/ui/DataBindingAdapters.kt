package io.github.anvell.keemobile.presentation.ui

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter
import io.github.anvell.keemobile.presentation.widgets.TagsCloudView

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

    @JvmStatic
    @BindingAdapter("onLongClick")
    fun setOnLongClick(view: View, listener: View.OnLongClickListener?) {
        listener?.let {
            view.setOnLongClickListener(listener)
        }
    }

    @JvmStatic
    @BindingAdapter("tags")
    fun setTags(view: TagsCloudView, tags: List<String>) {
        view.setTags(tags)
    }
}
