package io.github.anvell.keemobile.common.extensions

import androidx.lifecycle.SavedStateHandle
import io.github.anvell.keemobile.common.constants.Args

fun <T> SavedStateHandle.getArguments(): T {
    return get<T>(Args.KEY) ?: error("Arguments not defined.")
}
