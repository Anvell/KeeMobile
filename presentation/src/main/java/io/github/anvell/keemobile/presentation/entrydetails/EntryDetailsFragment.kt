package io.github.anvell.keemobile.presentation.entrydetails

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.github.anvell.keemobile.core.ui.mvi.MviComposeFragment
import io.github.anvell.keemobile.core.ui.theme.AppTheme

@AndroidEntryPoint
class EntryDetailsFragment :
    MviComposeFragment<EntryDetailsViewModel, EntryDetailsViewState, EntryDetailsCommand>() {
    override val viewModel: EntryDetailsViewModel by viewModels()

    @Composable
    override fun Content(
        state: EntryDetailsViewState,
        commands: (EntryDetailsCommand) -> Unit
    ) {
        AppTheme {
            EntryDetails(state, commands)
        }
    }
}
