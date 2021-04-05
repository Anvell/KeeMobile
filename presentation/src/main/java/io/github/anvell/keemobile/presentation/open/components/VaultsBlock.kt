package io.github.anvell.keemobile.presentation.open.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.imePadding
import io.github.anvell.keemobile.core.ui.components.PasswordTextField
import io.github.anvell.keemobile.core.ui.components.Spacers
import io.github.anvell.keemobile.core.ui.theme.AppTheme
import io.github.anvell.keemobile.domain.entity.FileListEntry
import io.github.anvell.keemobile.presentation.R

@Composable
internal fun VaultsBlock(
    selected: FileListEntry,
    files: List<FileListEntry>,
    isLoading: Boolean,
    onSelected: (FileListEntry) -> Unit,
    onUnlock: (FileListEntry, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val reversedFiles = remember(files) { files.reversed() }
    var password by rememberSaveable { mutableStateOf("") }

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

    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(
            items = reversedFiles,
            key = { it.fileSource.id }
        ) { item ->
            val color = animateColorAsState(
                if (item == selected) {
                    MaterialTheme.colors.onBackground.copy(alpha = 0.10f)
                } else {
                    Color.Transparent
                }
            )
            Row(
                modifier = Modifier
                    .clickable { onSelected(item) }
                    .background(color.value)
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.layout_horizontal_margin))
                    .padding(all = dimensionResource(R.dimen.list_item_vertical_padding))
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
                    tint = MaterialTheme.colors.onSurface.copy(alpha = 0.65f)
                )
            }
        }

        item { Spacer(Modifier.imePadding()) }
    }
}
