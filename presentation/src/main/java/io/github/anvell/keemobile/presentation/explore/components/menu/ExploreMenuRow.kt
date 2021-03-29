package io.github.anvell.keemobile.presentation.explore.components.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import io.github.anvell.keemobile.core.ui.components.Spacers
import io.github.anvell.keemobile.presentation.R

@Composable
internal fun ExploreMenuRow(
    title: String,
    icon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = dimensionResource(R.dimen.content_margin),
                vertical = dimensionResource(R.dimen.list_item_vertical_padding)
            )
    ) {
        if (icon != null) {
            icon()
            Spacers.S()
        }

        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colors.onSurface
        )
    }
}
