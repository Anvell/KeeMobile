package io.github.anvell.keemobile.core.ui.mvi

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class MviComposeViewModel<S, C>(initialState: S) : MviViewModel<S>(initialState) {
    private val pendingCommands = MutableSharedFlow<C>()

    init {
        viewModelScope.launch {
            pendingCommands.collect { onCommand(it) }
        }
    }

    abstract fun onCommand(command: C)

    fun emitCommand(command: C) {
        viewModelScope.launch {
            pendingCommands.emit(command)
        }
    }
}
