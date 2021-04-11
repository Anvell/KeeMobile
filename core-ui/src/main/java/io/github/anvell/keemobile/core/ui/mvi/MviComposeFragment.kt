package io.github.anvell.keemobile.core.ui.mvi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.accompanist.insets.ProvideWindowInsets
import io.github.anvell.keemobile.core.security.BiometricHelper
import io.github.anvell.keemobile.core.ui.locals.LocalAppNavigator
import io.github.anvell.keemobile.core.ui.locals.LocalBiometricHelper
import io.github.anvell.keemobile.core.ui.navigation.AppNavigatorImpl
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class MviComposeFragment<V, S, C> : Fragment() where V : MviComposeViewModel<S, C> {
    private val pendingCommands = MutableSharedFlow<C>()

    abstract val viewModel: V

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            pendingCommands.collect { onCommand(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = ComposeView(requireContext()).apply {
        setContent {
            viewModel.stateAsFlow()
                .collectAsState()
                .value
                ?.let { state ->
                    CompositionLocalProvider(
                        LocalAppNavigator provides AppNavigatorImpl(findNavController()),
                        LocalBiometricHelper provides BiometricHelper(requireActivity())
                    ) {
                        ProvideWindowInsets {
                            Content(
                                state = state,
                                commands = {
                                    lifecycleScope.launch { pendingCommands.emit(it) }
                                }
                            )
                        }
                    }
                }
        }
    }

    protected open fun onCommand(command: C) = viewModel.emitCommand(command)

    @Composable
    abstract fun Content(state: S, commands: (C) -> Unit)
}
