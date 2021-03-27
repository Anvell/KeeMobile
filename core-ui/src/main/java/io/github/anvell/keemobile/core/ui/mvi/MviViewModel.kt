@file:Suppress("unused")

package io.github.anvell.keemobile.core.ui.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.anvell.keemobile.domain.datatypes.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KProperty1

abstract class MviViewModel<S>(initialState: S) : ViewModel() {
    private val state = MutableStateFlow(initialState)

    fun observableState(): StateFlow<S> = state

    fun <T> withState(block: (S) -> T) = block(state.value)

    protected fun setState(reducer: S.() -> S) {
        state.value = reducer(state.value)
    }

    protected fun stateSubscribe(block: (S) -> Unit) {
        viewModelScope.launch {
            state.collect { block(it) }
        }
    }

    protected fun <P> selectSubscribe(property: KProperty1<S, P>, block: (P) -> Unit) {
        viewModelScope.launch {
            selectSubscribe(property).collect { block(it) }
        }
    }

    protected fun <P> selectSubscribe(property: KProperty1<S, P>): Flow<P> {
        return state.map { property.get(it) }.distinctUntilChanged()
    }

    protected suspend fun <V> Flow<Either<Throwable, V>>.execute(reducer: S.(Async<V>) -> S) {
        catch { setState { reducer(Fail(it)) } }
        collect {
            it.fold(
                left = { setState { reducer(Fail(it)) } },
                right = { setState { reducer(Success(it)) } }
            )
        }
    }

    protected suspend fun <V> Flow<V>.executeCatching(reducer: S.(Async<V>) -> S) {
        catch { setState { reducer(Fail(it)) } }
        collect { setState { reducer(Success(it)) } }
    }

    protected fun <V> execute(
        block: suspend S.() -> Either<Throwable, V>,
        reducer: S.(Async<V>) -> S
    ) {
        viewModelScope.launch {
            setState { reducer(Loading) }

            block(state.value).fold(
                left = { setState { reducer(Fail(it)) } },
                right = { setState { reducer(Success(it)) } }
            )
        }
    }

    protected fun <V> executeCatching(
        block: suspend S.() -> V,
        reducer: S.(Async<V>) -> S
    ) {
        viewModelScope.launch {
            setState { reducer(Loading) }

            try {
                val result = block(state.value)
                setState { reducer(Success(result)) }
            } catch (error: Throwable) {
                setState { reducer(Fail(error)) }
            }
        }
    }

    protected inline fun <reified P> SavedStateHandle.rememberAsJson(property: KProperty1<S, P>) {
        selectSubscribe(property) { this[property.name] = Json.encodeToString(it) }
    }

    companion object {
        inline fun <S, reified P> SavedStateHandle.retrieveFromJson(property: KProperty1<S, P>): P? {
            return get<String>(property.name)?.let {
                Json { coerceInputValues = true }.decodeFromString(it)
            }
        }
    }
}
