package io.github.anvell.keemobile.common.extensions

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.addDivider(context: Context, @DrawableRes id: Int, orientation: Int) {
    val divider = DividerItemDecoration(context, orientation)
    val drawable = ContextCompat.getDrawable(context, id)

    drawable?.let {
        divider.setDrawable(it)
        addItemDecoration(divider)
    }
}
