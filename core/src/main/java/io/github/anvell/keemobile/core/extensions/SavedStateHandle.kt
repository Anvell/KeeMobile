@file:Suppress("unused")

package io.github.anvell.keemobile.core.extensions

import androidx.lifecycle.SavedStateHandle
import io.github.anvell.keemobile.core.constants.Args
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun <T> SavedStateHandle.getArguments(): T {
    return get<T>(Args.Key) ?: error("Arguments not defined.")
}

fun <T> SavedStateHandle.getArgumentsOrNull(): T? {
    return get<T>(Args.Key)
}

inline fun <reified T> SavedStateHandle.getJsonArguments(): T {
    return requireNotNull(getJsonArgumentsOrNull()) { "Arguments not defined." }
}

inline fun <reified T> SavedStateHandle.getJsonArgumentsOrNull(): T? {
    return get<String>(Args.Key)?.let {
        Json { coerceInputValues = true }.decodeFromString(it)
    }
}

inline fun <reified T> SavedStateHandle.updateArguments(value: T) {
    this[Args.Key] = value
}

inline fun <reified T> SavedStateHandle.updateJsonArguments(value: T) {
    updateArguments(Json.encodeToString(value))
}
