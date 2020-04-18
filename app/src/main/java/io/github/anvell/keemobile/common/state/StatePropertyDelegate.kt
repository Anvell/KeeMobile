package io.github.anvell.keemobile.common.state

import android.os.Bundle
import io.github.anvell.keemobile.common.extensions.put
import kotlin.reflect.KProperty

abstract class StatePropertyDelegate<T> {

    operator fun setValue(thisRef: StateStore, property: KProperty<*>, value: T) {
        thisRef.stateBundle.put(property.name, value)
    }

    protected fun retrieveValue(stateBundle: Bundle, key: String) = stateBundle.get(key) as T?
}

class NullableStateProperty<T> : StatePropertyDelegate<T>() {
    operator fun getValue(thisRef: StateStore, property: KProperty<*>): T? =
        retrieveValue(thisRef.stateBundle, property.name)
}

class StateProperty<T>(private val defaultValue: T) : StatePropertyDelegate<T>() {
    operator fun getValue(thisRef: StateStore, property: KProperty<*>): T =
        retrieveValue(thisRef.stateBundle, property.name) ?: defaultValue
}
