package io.github.anvell.keemobile.core.extensions

import androidx.lifecycle.SavedStateHandle
import io.github.anvell.keemobile.core.constants.Args

fun <T> SavedStateHandle.getArguments(): T {
    return get<T>(Args.KEY) ?: error("Arguments not defined.")
}
