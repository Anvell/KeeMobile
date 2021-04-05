package io.github.anvell.keemobile.presentation.open.components

import android.net.Uri
import androidx.activity.compose.registerForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.anvell.keemobile.core.extensions.persistReadWritePermissions
import io.github.anvell.keemobile.domain.entity.FileListEntry
import io.github.anvell.keemobile.domain.entity.FileListEntrySecrets
import io.github.anvell.keemobile.presentation.R

private const val FileFilter = "*/*"

private val DockButtonPadding = 16.dp
private val DockIconSize = 24.dp
private val DockElevation = 4.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun Dock(
    selected: FileListEntry?,
    onDocumentCreated: (Uri) -> Unit,
    onDocumentOpened: (Uri) -> Unit,
    onUnlockWithBiometrics: (FileListEntrySecrets.Some) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val defaultFileName = stringResource(R.string.open_default_file_name)
    val createDocumentContract = remember { ActivityResultContracts.CreateDocument() }
    val createDocumentLauncher = registerForActivityResult(createDocumentContract) { uri: Uri? ->
        if (uri != null) {
            uri.persistReadWritePermissions(context)
            onDocumentCreated(uri)
        }
    }

    val openDocumentContract = remember { ActivityResultContracts.OpenDocument() }
    val openDocumentLauncher = registerForActivityResult(openDocumentContract) { uri: Uri? ->
        if (uri != null) {
            uri.persistReadWritePermissions(context)
            onDocumentOpened(uri)
        }
    }

    Surface(
        elevation = DockElevation,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            DockButton(
                iconPainter = painterResource(R.drawable.ic_plus_square),
                onClick = { createDocumentLauncher.launch(defaultFileName) },
                modifier = Modifier
                    .semantics {
                        contentDescription = context.getString(R.string.open_button_label_create)
                    }
                    .weight(1f)
            )

            DockButton(
                iconPainter = painterResource(R.drawable.ic_folder),
                onClick = { openDocumentLauncher.launch(arrayOf(FileFilter)) },
                modifier = Modifier
                    .semantics {
                        contentDescription = context.getString(R.string.open_button_label_open)
                    }
                    .weight(1f)
            )

            AnimatedVisibility(
                visible = selected != null,
                modifier = Modifier.weight(1f)
            ) {
                DockButton(
                    iconPainter = rememberVectorPainter(Icons.Default.VpnKey),
                    enabled = false,
                    onClick = {},
                    modifier = Modifier
                        .semantics {
                            contentDescription = context.getString(
                                R.string.open_button_label_use_keyfile
                            )
                        }
                )
            }

            AnimatedVisibility(
                visible = selected?.encryptedSecrets is FileListEntrySecrets.Some,
                modifier = Modifier.weight(1f)
            ) {
                DockButton(
                    iconPainter = rememberVectorPainter(Icons.Default.Fingerprint),
                    enabled = false,
                    onClick = {
                        (selected?.encryptedSecrets as? FileListEntrySecrets.Some)?.let {
                            onUnlockWithBiometrics(it)
                        }
                    },
                    modifier = Modifier
                        .semantics {
                            contentDescription = context.getString(
                                R.string.open_button_label_use_biometrics
                            )
                        }
                )
            }
        }
    }
}

@Composable
private fun DockButton(
    iconPainter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = MaterialTheme.colors.onSurface
) {
    Box(
        modifier = modifier
            .clickable(
                onClick = onClick,
                enabled = enabled,
                role = Role.Button
            )
            .padding(DockButtonPadding),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = iconPainter,
            contentDescription = null,
            tint = tint.copy(
                alpha = if (enabled) {
                    ContentAlpha.high
                } else {
                    ContentAlpha.disabled
                }
            ),
            modifier = Modifier.size(DockIconSize)
        )
    }
}
