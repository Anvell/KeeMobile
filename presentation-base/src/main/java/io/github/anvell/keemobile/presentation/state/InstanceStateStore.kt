package io.github.anvell.keemobile.presentation.state

import android.os.Bundle

interface InstanceStateStore {
    val stateBundle: Bundle

    fun <T> stateProperty() = NullableStateProperty<T>()

    fun <T> stateProperty(defaultValue: T) = StateProperty(defaultValue)
}
