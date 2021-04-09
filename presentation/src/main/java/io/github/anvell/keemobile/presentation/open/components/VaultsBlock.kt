package io.github.anvell.keemobile.presentation.open.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.anvell.keemobile.core.ui.components.PasswordTextField
import io.github.anvell.keemobile.core.ui.components.Spacers
import io.github.anvell.keemobile.core.ui.theme.AppTheme
import io.github.anvell.keemobile.domain.entity.FileListEntry
import io.github.anvell.keemobile.presentation.R

private const val ItemIconAlpha = 0.65f

private val ItemElevation = 2.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun VaultsBlock(
    selected: FileListEntry,
    files: List<FileListEntry>,
    isLoading: Boolean,
    onSelected: (FileListEntry) -> Unit,
    onUnlock: (FileListEntry, String) -> Unit,
    onDismiss: (FileListEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val reversedFiles = remember(files) { files.reversed() }
    var password by rememberSaveable { mutableStateOf("") }

    Column(modifier) {
        Text(
            text = selected.fileSource.nameWithoutExtension,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.h4.copy(
                color = MaterialTheme.colors.onBackground
            ),
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.layout_horizontal_margin)
            )
        )
        Spacers.Xl()

        Row(
            Modifier
                .padding(horizontal = dimensionResource(R.dimen.layout_horizontal_margin))
                .background(MaterialTheme.colors.surface, MaterialTheme.shapes.small)
                .height(IntrinsicSize.Min)
        ) {
            AppTheme.PasswordTextField(
                value = password,
                onValueChange = { password = it },
                hint = { Text(stringResource(R.string.open_text_label_password)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                    onGo = { onUnlock(selected, password) }
                ),
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = { onUnlock(selected, password) },
                elevation = null,
                enabled = password.isNotBlank() && !isLoading,
                shape = MaterialTheme.shapes.small.copy(
                    topStart = ZeroCornerSize,
                    bottomStart = ZeroCornerSize
                ),
                modifier = Modifier.fillMaxHeight()
            ) {
                Crossfade(isLoading) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_unlock),
                            contentDescription = null,
                            tint = MaterialTheme.colors.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
        Spacers.L()

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(
                items = reversedFiles,
                key = { it.fileSource.id }
            ) { item ->
                val dismissState = rememberDismissState(
                    confirmStateChange = { value ->
                        if (value == DismissValue.DismissedToStart) onDismiss(item)
                        value == DismissValue.DismissedToStart
                    }
                )
                val backgroundColor by animateColorAsState(
                    if (item == selected) {
                        MaterialTheme.colors.surface
                    } else {
                        MaterialTheme.colors.background
                    }
                )

                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart),
                    background = {
                        val color by animateColorAsState(
                            targetValue = if (dismissState.dismissDirection == DismissDirection.EndToStart) {
                                MaterialTheme.colors.error.copy(alpha = 0.8f)
                            } else {
                                MaterialTheme.colors.background
                            },
                            animationSpec = tween(durationMillis = 800)
                        )

                        Box(
                            contentAlignment = Alignment.CenterEnd,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .background(color)
                                .fillMaxSize()
                                .padding(horizontal = dimensionResource(R.dimen.layout_horizontal_margin))
                                .padding(vertical = dimensionResource(R.dimen.list_item_vertical_padding))
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_close),
                                contentDescription = null,
                                tint = MaterialTheme.colors.onError.copy(alpha = ItemIconAlpha)
                            )
                        }
                    },
                    dismissContent = {
                        val isDragged = dismissState.dismissDirection != null
                        val elevation by animateDpAsState(if (isDragged) ItemElevation else 0.dp)

                        Surface(
                            color = backgroundColor,
                            shape = MaterialTheme.shapes.medium,
                            elevation = elevation
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelected(item) }
                                    .padding(horizontal = dimensionResource(R.dimen.layout_horizontal_margin))
                                    .padding(vertical = dimensionResource(R.dimen.list_item_vertical_padding))
                            ) {
                                Text(
                                    text = item.fileSource.nameWithoutExtension,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacers.M()

                                Icon(
                                    painter = painterResource(R.drawable.ic_arrow_simple_forward),
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.onSurface.copy(alpha = ItemIconAlpha)
                                )
                            }
                        }
                    },
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.layout_horizontal_margin)
                    )
                )
                Spacers.Xs()
            }
        }
    }
}
