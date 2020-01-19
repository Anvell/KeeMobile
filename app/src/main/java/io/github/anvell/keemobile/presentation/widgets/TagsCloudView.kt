package io.github.anvell.keemobile.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import io.github.anvell.keemobile.R

class TagsCloudView(context: Context, attrs: AttributeSet?) : ChipGroup(context, attrs) {

    fun setTags(tags: List<String>) {
        removeAllViews()
        tags.map {
            (View.inflate(context, R.layout.item_chip, null) as Chip).apply {
                text = it
            }
        }.onEach { addView(it) }
    }

}
