package io.github.anvell.keemobile.presentation.entrydetails.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.anvell.keemobile.core.ui.components.Spacers
import io.github.anvell.keemobile.presentation.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MaskedPropertyRow(
    title: String,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    var isMasked by rememberSaveable { mutableStateOf(true) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onLongClick = onLongClick,
                onClick = {}
            )
            .background(MaterialTheme.colors.surface)
            .padding(
                start = dimensionResource(R.dimen.content_margin),
                end = 6.dp
            )
            .padding(vertical = dimensionResource(R.dimen.content_margin))
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

            if (isMasked) {
                Text(
                    text = stringResource(R.string.details_masked_content),
                    letterSpacing = 1.15.sp
                )
            } else {
                content()
            }
        }
        Spacers.S()

        IconButton(onClick = { isMasked = !isMasked }) {
            Icon(
                painter = painterResource(
                    if (isMasked) {
                        R.drawable.ic_eye_show
                    } else {
                        R.drawable.ic_eye_hide
                    }
                ),
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface
            )
        }
    }
}
