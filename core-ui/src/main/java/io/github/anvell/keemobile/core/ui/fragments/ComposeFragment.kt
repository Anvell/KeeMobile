@file:Suppress("unused")

package io.github.anvell.keemobile.core.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.accompanist.insets.ProvideWindowInsets
import io.github.anvell.keemobile.core.security.BiometricHelper
import io.github.anvell.keemobile.core.ui.locals.LocalAppNavigator
import io.github.anvell.keemobile.core.ui.locals.LocalBiometricHelper
import io.github.anvell.keemobile.core.ui.navigation.AppNavigatorImpl

abstract class ComposeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = ComposeView(requireContext()).apply {
        setContent {
            CompositionLocalProvider(
                LocalAppNavigator provides AppNavigatorImpl(findNavController()),
                LocalBiometricHelper provides BiometricHelper(requireActivity())
            ) {
                ProvideWindowInsets { Content() }
            }
        }
    }

    @Composable
    abstract fun Content()
}
