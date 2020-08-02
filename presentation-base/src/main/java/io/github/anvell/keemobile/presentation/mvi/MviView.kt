package io.github.anvell.keemobile.presentation.mvi

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe

interface MviView<V, S> : LifecycleOwner where V : MviViewModel<S> {
    val viewModel: V

    fun stateSubscribe(lifecycleOwner: LifecycleOwner) {
        viewModel.observableState().observe(lifecycleOwner) { render(it) }
    }

    fun render(state: S)
}
