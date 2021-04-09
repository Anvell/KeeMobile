package io.github.anvell.keemobile.presentation.explore.components.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.anvell.either.Right
import io.github.anvell.keemobile.core.ui.components.Spacers
import io.github.anvell.keemobile.core.ui.locals.LocalBiometricHelper
import io.github.anvell.keemobile.domain.entity.*
import io.github.anvell.keemobile.presentation.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ExploreMenu(
    selected: OpenDatabase,
    selectedEncryptedSecrets: FileListEntrySecrets,
    items: List<OpenDatabase>,
    onItemSelected: (VaultId) -> Unit,
    onCloseItem: (VaultId) -> Unit,
    onOpen: () -> Unit,
    onUseBiometrics: (source: FileSource, encryptedSecrets: FileListEntrySecrets) -> Unit,
    modifier: Modifier = Modifier
) {
    val biometricHelper = LocalBiometricHelper.current

    Column(
        modifier.width(dimensionResource(R.dimen.dialog_maximum_width))
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colors.background)
        ) {
            Text(
                text = selected.source.nameWithoutExtension,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(R.dimen.content_margin),
                    vertical = dimensionResource(R.dimen.list_item_vertical_padding)
                )
            )

            items.filter { it.id != selected.id }
                .forEach { item ->
                    Divider()
                    ExploreMenuRow(
                        title = item.source.nameWithoutExtension,
                        padding = PaddingValues(
                            start = dimensionResource(R.dimen.content_margin),
                            end = 4.dp
                        ),
                        onClick = { onItemSelected(item.id) },
                        trailing = {
                            IconButton(onClick = { onCloseItem(item.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.explore_menu_row_button_close),
                                    tint = MaterialTheme.colors.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        modifier = Modifier.background(MaterialTheme.colors.background)
                    )
                }
        }
        Spacers.M()

        Column(
            Modifier
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colors.background)
        ) {
            ExploreMenuRow(
                title = stringResource(R.string.explore_menu_row_open_another),
                leading = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onSurface
                    )
                },
                onClick = onOpen
            )
            Divider()

            if (selected.secrets !is KeyFileOnly && biometricHelper.canAuthenticate() is Right) {
                UseBiometricsRow(
                    database = selected,
                    encryptedSecrets = selectedEncryptedSecrets,
                    onClick = { onUseBiometrics(selected.source, it) }
                )
            }
        }
    }
}
