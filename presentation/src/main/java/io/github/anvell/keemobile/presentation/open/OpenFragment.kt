package io.github.anvell.keemobile.presentation.open

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.github.anvell.keemobile.core.ui.mvi.MviComposeFragment
import io.github.anvell.keemobile.core.ui.theme.AppTheme

@AndroidEntryPoint
class OpenFragment : MviComposeFragment<OpenViewModel, OpenViewState, OpenCommand>() {
    override val viewModel: OpenViewModel by viewModels()

    @Composable
    override fun Content(
        state: OpenViewState,
        commands: (OpenCommand) -> Unit
    ) {
        AppTheme {
            Open(state, commands)
        }
    }
}
