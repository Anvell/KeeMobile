package io.github.anvell.keemobile.core.ui.mvi

import androidx.lifecycle.LifecycleOwner

interface MviRxView<V, S> : LifecycleOwner where V : MviRxViewModel<S> {
    val viewModel: V

    fun stateSubscribe(lifecycleOwner: LifecycleOwner) {
        viewModel.observableState().observe(lifecycleOwner) { render(it) }
    }

    fun render(state: S)
}
