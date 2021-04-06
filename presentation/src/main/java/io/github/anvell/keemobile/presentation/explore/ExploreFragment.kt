package io.github.anvell.keemobile.presentation.explore

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.github.anvell.keemobile.core.ui.mvi.MviComposeFragment
import io.github.anvell.keemobile.core.ui.theme.AppTheme

@AndroidEntryPoint
class ExploreFragment : MviComposeFragment<ExploreViewModel, ExploreViewState, ExploreCommand>() {
    override val viewModel: ExploreViewModel by viewModels()

    @Composable
    override fun Content(
        state: ExploreViewState,
        commands: (ExploreCommand) -> Unit
    ) {
        AppTheme {
            Explore(state, commands)
        }
    }
}
