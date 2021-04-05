package io.github.anvell.keemobile.presentation.entrydetails.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import io.github.anvell.keemobile.core.ui.components.Spacers
import io.github.anvell.keemobile.presentation.R

private const val TagSeparator = ", "

private val TagPadding = 8.dp
private val TagCornerRadius = 6.dp
private val TagBorderWidth = 0.5.dp

@Composable
internal fun TagsPropertyRow(
    tags: List<String>,
    modifier: Modifier = Modifier,
    onLongClick: ((String) -> Unit)? = null
) {
    PropertyRow(
        title = stringResource(R.string.details_tags),
        onLongClick = { onLongClick?.invoke(tags.joinToString(TagSeparator)) },
        modifier = modifier
    ) {
        FlowRow {
            tags.forEach {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier
                        .padding(top = TagPadding)
                        .border(
                            width = TagBorderWidth,
                            color = MaterialTheme.colors.onSurface,
                            shape = RoundedCornerShape(TagCornerRadius)
                        )
                        .padding(TagPadding)
                )
                Spacers.Xs()
            }
        }
    }
}
