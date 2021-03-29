package io.github.anvell.keemobile.presentation.explore.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Source
import androidx.compose.material.icons.outlined.ViewDay
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.animatedVectorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.github.anvell.keemobile.core.ui.components.TextField
import io.github.anvell.keemobile.core.ui.theme.AppTheme
import io.github.anvell.keemobile.domain.entity.ViewMode
import io.github.anvell.keemobile.presentation.R

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalAnimationApi::class
)
@Composable
internal fun SearchTextField(
    value: String,
    viewMode: ViewMode,
    leading: @Composable () -> Unit,
    onValueChange: (String) -> Unit,
    onViewModeChange: (ViewMode) -> Unit,
    onMoreClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val (viewModeLabel, viewModeIcon) = when (viewMode) {
        ViewMode.TREE -> stringResource(R.string.explore_button_show_list) to Icons.Outlined.ViewDay
        ViewMode.LIST -> stringResource(R.string.explore_button_show_folders) to Icons.Outlined.Source
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.background(
            MaterialTheme.colors.surface,
            MaterialTheme.shapes.medium
        )
    ) {
        leading()

        AppTheme.TextField(
            value = value,
            onValueChange = onValueChange,
            hint = { Text(stringResource(R.string.explore_text_hint_search)) },
            trailing = {
                Row {
                    AnimatedVisibility(value.isEmpty()) {
                        IconButton(
                            onClick = { onViewModeChange(!viewMode) },
                            modifier = Modifier.semantics {
                                contentDescription = viewModeLabel
                            }
                        ) {
                            Icon(
                                imageVector = viewModeIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colors.onSurface
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            if (value.isNotEmpty()) {
                                onValueChange("")
                            } else {
                                onMoreClicked()
                            }
                        }
                    ) {
                        Icon(
                            painter = animatedVectorResource(R.drawable.ic_anim_close_to_more)
                                .painterFor(value.isEmpty()),
                            contentDescription = null,
                            tint = MaterialTheme.colors.onSurface
                        )
                    }

                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { keyboardController?.hideSoftwareKeyboard() }
            ),
            horizontalPadding = 8.dp
        )
    }
}
