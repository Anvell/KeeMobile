package io.github.anvell.keemobile.presentation.open.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import com.google.accompanist.insets.LocalWindowInsets
import io.github.anvell.keemobile.core.extensions.getName
import io.github.anvell.keemobile.core.extensions.toSha256
import io.github.anvell.keemobile.core.ui.locals.LocalBiometricHelper
import io.github.anvell.keemobile.domain.datatypes.map
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.domain.entity.KeyOnly
import io.github.anvell.keemobile.domain.entity.Secret
import io.github.anvell.keemobile.presentation.R
import io.github.anvell.keemobile.presentation.open.OpenCommand
import io.github.anvell.keemobile.presentation.open.OpenViewState
import kotlinx.coroutines.launch

@Composable
internal fun DockBlock(
    state: OpenViewState,
    commands: (OpenCommand) -> Unit
) {
    val context = LocalContext.current
    val biometricHelper = LocalBiometricHelper.current
    val windowInsets = LocalWindowInsets.current
    val coroutineScope = rememberCoroutineScope()

    if (!windowInsets.ime.isVisible) {
        Box(
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.layout_horizontal_margin))
                .padding(top = dimensionResource(R.dimen.content_margin))
        ) {
            Dock(
                selected = state.selectedFile,
                onDocumentCreated = {
                    val uri = it.toString()
                    val fileName = it.getName(context) ?: ""

                    commands(
                        OpenCommand.CreateFile(
                            source = FileSource.Storage(uri.toSha256(), fileName, uri),
                            secrets = KeyOnly(Secret.Unencrypted("123"))
                        )
                    )
                },
                onDocumentOpened = {
                    val uri = it.toString()
                    val fileName = it.getName(context)

                    if (fileName != null) {
                        commands(
                            OpenCommand.AddFileSource(
                                source = FileSource.Storage(uri.toSha256(), fileName, uri)
                            )
                        )
                    }
                },
                onUnlockWithBiometrics = { secrets ->
                    require(secrets.fileSecrets is KeyOnly) { "Only password protection is supported." }

                    coroutineScope.launch {
                        val unencryptedSecret = biometricHelper.authenticateAndDecrypt(
                            secret = (secrets.fileSecrets as KeyOnly).masterKey as Secret.Encrypted,
                            title = context.getString(R.string.open_dialogs_biometrics_title_unlock),
                            cancelLabel = context.getString(R.string.open_dialogs_biometrics_label_cancel)
                        )

                        unencryptedSecret.map { data ->
                            if (data != null) {
                                commands(
                                    OpenCommand.OpenFile(
                                        entry = state.selectedFile!!,
                                        secrets = KeyOnly(data)
                                    )
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}
