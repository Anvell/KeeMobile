package io.github.anvell.keemobile.common.state

import android.os.Bundle
import io.github.anvell.keemobile.common.extensions.put
import kotlin.reflect.KProperty

abstract class StatePropertyDelegate<T> {

    private var field: T? = null

    operator fun setValue(thisRef: StateHandler, property: KProperty<*>, value: T) {
        if (field != value) {
            field = value
            thisRef.stateBundle.put(property.name, value)
        }
    }

    protected fun retrieveValue(stateBundle: Bundle, key: String): T? =
        field ?: (stateBundle.get(key) as T?).apply { field = this }
}

class NullableStateProperty<T> : StatePropertyDelegate<T>() {
    operator fun getValue(thisRef: StateHandler, property: KProperty<*>): T? =
        retrieveValue(thisRef.stateBundle, property.name)
}

class StateProperty<T>(private val defaultValue: T) : StatePropertyDelegate<T>() {
    operator fun getValue(thisRef: StateHandler, property: KProperty<*>): T =
        retrieveValue(thisRef.stateBundle, property.name) ?: defaultValue
}
