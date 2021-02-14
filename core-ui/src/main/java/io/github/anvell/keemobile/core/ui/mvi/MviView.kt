package io.github.anvell.keemobile.core.ui.mvi

import androidx.lifecycle.LifecycleOwner

interface MviView<V, S> : LifecycleOwner where V : MviViewModel<S> {
    val viewModel: V

    fun stateSubscribe(lifecycleOwner: LifecycleOwner) {
        viewModel.observableState().observe(lifecycleOwner) { render(it) }
    }

    fun render(state: S)
}
