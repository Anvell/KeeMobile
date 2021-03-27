package io.github.anvell.keemobile.presentation.explore

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.github.anvell.keemobile.core.security.BiometricHelper
import io.github.anvell.keemobile.core.ui.mvi.MviComposeFragment
import io.github.anvell.keemobile.core.ui.theme.AppTheme
import javax.inject.Inject

@AndroidEntryPoint
class ExploreFragment : MviComposeFragment<ExploreViewModel, ExploreViewState, ExploreCommand>() {
    override val viewModel: ExploreViewModel by viewModels()

    @Inject
    lateinit var biometricHelper: BiometricHelper

    @Composable
    override fun Content(
        state: ExploreViewState,
        commands: (ExploreCommand) -> Unit
    ) {
        AppTheme {
            Explore(state, commands, biometricHelper)
        }
    }
}
