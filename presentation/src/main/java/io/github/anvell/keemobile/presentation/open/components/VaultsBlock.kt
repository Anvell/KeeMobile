package io.github.anvell.keemobile.presentation.open.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
        )
    )
    Spacers.Xl()

    Row(
        Modifier
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

    Column(modifier.verticalScroll(rememberScrollState())) {
        Surface(
            elevation = 4.dp,
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .fillMaxWidth()
        ) {
            Column {
                reversedFiles.forEach {
                    val color = animateColorAsState(
                        if (it == selected) {
                            MaterialTheme.colors.onBackground.copy(alpha = 0.10f)
                        } else {
                            Color.Transparent
                        }
                    )
                    Box(
                        modifier = Modifier
                            .clickable { onSelected(it) }
                            .background(color.value)
                            .fillMaxWidth()
                            .padding(dimensionResource(R.dimen.list_item_vertical_padding))
                    ) {
                        Text(
                            text = it.fileSource.nameWithoutExtension,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Divider(color = MaterialTheme.colors.background)
                }
            }
        }

        Spacer(Modifier.imePadding())
    }
}
