package io.github.anvell.keemobile.presentation.open

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import io.github.anvell.keemobile.core.extensions.getName
import io.github.anvell.keemobile.core.extensions.toSha256
import io.github.anvell.keemobile.core.ui.locals.LocalAppNavigator
import io.github.anvell.keemobile.core.ui.locals.LocalBiometricHelper
import io.github.anvell.keemobile.domain.datatypes.Fail
import io.github.anvell.keemobile.domain.datatypes.Loading
import io.github.anvell.keemobile.domain.datatypes.Success
import io.github.anvell.keemobile.domain.datatypes.map
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.domain.entity.KeyOnly
import io.github.anvell.keemobile.domain.entity.Secret
import io.github.anvell.keemobile.presentation.R
import io.github.anvell.keemobile.presentation.open.components.Dock
import io.github.anvell.keemobile.presentation.open.components.LandingBlock
import io.github.anvell.keemobile.presentation.open.components.VaultsBlock
import kotlinx.coroutines.launch

private val DockTopPadding = 32.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Open(
    state: OpenViewState,
    commands: (OpenCommand) -> Unit
) {
    val context = LocalContext.current
    val navigator = LocalAppNavigator.current
    val biometricHelper = LocalBiometricHelper.current
    val windowInsets = LocalWindowInsets.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(
                    horizontal = dimensionResource(R.dimen.layout_horizontal_margin),
                    vertical = dimensionResource(R.dimen.layout_vertical_margin)
                )
        ) {
            when (val recentFiles = state.recentFiles) {
                is Success -> {
                    if (recentFiles().isNotEmpty()) {
                        VaultsBlock(
                            selected = state.selectedFile ?: recentFiles().first(),
                            files = recentFiles(),
                            isLoading = state.openFile is Loading,
                            onSelected = {
                                commands(OpenCommand.SelectFile(it))
                            },
                            onUnlock = { entry, password ->
                                commands(
                                    OpenCommand.OpenFile(
                                        entry = entry,
                                        secrets = KeyOnly(Secret.Unencrypted(password))
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        LandingBlock(Modifier.weight(1f))
                    }
                }
                else -> Unit
            }

            AnimatedVisibility(
                visible = state.recentFiles is Success && !windowInsets.ime.isVisible,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(top = DockTopPadding)
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

    LaunchedEffect(state.openFile) {
        when {
            state.openFile is Fail && !state.openFile.isConsumed -> {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.error_wrong_master_key)
                )
                state.openFile.error
            }
            state.openFile is Success && !state.openFile.isConsumed -> {
                navigator.navigate(
                    id = R.id.action_explore_database,
                    data = state.openFile.unwrap()
                )
            }
        }
    }
}
