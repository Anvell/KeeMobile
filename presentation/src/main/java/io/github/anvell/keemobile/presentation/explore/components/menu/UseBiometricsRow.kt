package io.github.anvell.keemobile.presentation.explore.components.menu

import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import io.github.anvell.keemobile.core.ui.locals.LocalBiometricHelper
import io.github.anvell.either.map
import io.github.anvell.keemobile.domain.entity.*
import io.github.anvell.keemobile.presentation.R
import kotlinx.coroutines.launch

@Composable
internal fun UseBiometricsRow(
    database: OpenDatabase,
    encryptedSecrets: FileListEntrySecrets,
    onClick: (FileListEntrySecrets) -> Unit
) {
    val context = LocalContext.current
    val biometricHelper = LocalBiometricHelper.current
    val coroutineScope = rememberCoroutineScope()

    if (encryptedSecrets is FileListEntrySecrets.Some) {
        ExploreMenuRow(
            title = stringResource(R.string.explore_menu_row_use_biometrics_remove),
            leading = {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface
                )
            },
            onClick = { onClick(FileListEntrySecrets.Unspecified) }
        )
    } else {
        ExploreMenuRow(
            title = stringResource(R.string.explore_menu_row_use_biometrics),
            leading = {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface
                )
            },
            onClick = {
                coroutineScope.launch {
                    val encryptedSecret = biometricHelper.authenticateAndEncrypt(
                        secret = (database.secrets as KeyOnly).masterKey as Secret.Unencrypted,
                        title = context.getString(R.string.open_dialogs_biometrics_title_set),
                        cancelLabel = context.getString(R.string.open_dialogs_biometrics_label_cancel)
                    )

                    encryptedSecret.map { data ->
                        if (data != null) {
                            onClick(
                                FileListEntrySecrets.Some(
                                    when (val secrets = database.secrets) {
                                        is KeyFileOnly -> error("Cannot assign biometrics when using key file only.")
                                        is KeyOnly -> KeyOnly(data)
                                        is KeyWithKeyFile -> secrets.copy(masterKey = data)
                                    }
                                )
                            )
                        }
                    }
                }
            }
        )
    }
}
