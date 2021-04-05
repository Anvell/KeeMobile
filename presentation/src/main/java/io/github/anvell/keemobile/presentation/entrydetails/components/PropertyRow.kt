package io.github.anvell.keemobile.presentation.entrydetails.components

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.anvell.keemobile.core.ui.components.Spacers
import io.github.anvell.keemobile.presentation.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PropertyRow(
    title: String,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(
        horizontal = dimensionResource(R.dimen.content_margin),
        vertical = dimensionResource(R.dimen.content_margin)
    ),
    trailing: @Composable (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    background: Color = MaterialTheme.colors.surface,
    content: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onLongClick = onLongClick,
                onClick = {}
            )
            .background(background)
            .padding(padding)
            .fillMaxWidth()
            .then(modifier)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.caption
            )
            Spacers.Xxs()

            content()
        }

        if (trailing != null) {
            Spacers.S()
            trailing()
        }
    }
}
