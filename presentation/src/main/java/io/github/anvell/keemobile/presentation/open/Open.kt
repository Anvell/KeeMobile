package io.github.anvell.keemobile.presentation.open

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding
import io.github.anvell.async.Fail
import io.github.anvell.async.Loading
import io.github.anvell.async.Success
import io.github.anvell.keemobile.core.ui.locals.LocalAppNavigator
import io.github.anvell.keemobile.domain.entity.KeyOnly
import io.github.anvell.keemobile.domain.entity.Secret
import io.github.anvell.keemobile.presentation.R
import io.github.anvell.keemobile.presentation.open.components.DockBlock
import io.github.anvell.keemobile.presentation.open.components.LandingBlock
import io.github.anvell.keemobile.presentation.open.components.VaultsBlock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Open(
    state: OpenViewState,
    commands: (OpenCommand) -> Unit
) {
    val context = LocalContext.current
    val navigator = LocalAppNavigator.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
        modifier = Modifier.navigationBarsWithImePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(vertical = dimensionResource(R.dimen.layout_vertical_margin))
        ) {
            if (state.recentFiles is Success) {
                val recentFiles = state.recentFiles.unwrap()

                if (recentFiles.isNotEmpty()) {
                    VaultsBlock(
                        selected = state.selectedFile ?: recentFiles.first(),
                        files = recentFiles,
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
                        onDismiss = {
                            commands(OpenCommand.RemoveRecentFile(it))
                        },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    LandingBlock(Modifier.weight(1f))
                }

                DockBlock(state, commands)
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
                    data = Json.encodeToString(state.openFile.unwrap())
                )
            }
        }
    }
}
