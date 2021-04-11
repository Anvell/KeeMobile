@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.github.anvell.keemobile.core.ui.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.anvell.async.Async
import io.github.anvell.async.Fail
import io.github.anvell.async.Loading
import io.github.anvell.async.Success
import io.github.anvell.either.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KProperty1

abstract class MviViewModel<S>(initialState: S) : ViewModel() {
    private val state = MutableStateFlow(initialState)

    fun stateAsFlow(): StateFlow<S> = state

    fun <T> withState(block: (S) -> T) = block(state.value)

    protected fun setState(reducer: S.() -> S) {
        state.value = reducer(state.value)
    }

    protected fun <P> selectSubscribe(
        property: KProperty1<S, P>,
        block: (P) -> Unit
    ) {
        viewModelScope.launch {
            selectAsFlow(property).collect { block(it) }
        }
    }

    protected fun <P> selectAsFlow(
        property: KProperty1<S, P>
    ): Flow<P> = state
        .map { property.get(it) }
        .distinctUntilChanged()

    protected suspend fun <V> Flow<Either<Throwable, V>>.collectReduceAsState(
        reducer: S.(Async<V>) -> S
    ) {
        catch { setState { reducer(Fail(it)) } }
        collect {
            it.fold(
                left = { setState { reducer(Fail(it)) } },
                right = { setState { reducer(Success(it)) } }
            )
        }
    }

    protected suspend fun <V> Flow<V>.collectAsState(
        reducer: S.(Async<V>) -> S
    ) {
        catch { setState { reducer(Fail(it)) } }
        collect { setState { reducer(Success(it)) } }
    }

    protected fun <V> Deferred<Either<Throwable, V>>.reduceAsState(
        reducer: S.(Async<V>) -> S
    ) = viewModelScope.launch {
        setState { reducer(Loading) }

        this@reduceAsState.await().fold(
            left = { setState { reducer(Fail(it)) } },
            right = { setState { reducer(Success(it)) } }
        )
    }

    protected fun <V> Deferred<V>.catchAsState(
        reducer: S.(Async<V>) -> S
    ) = viewModelScope.launch {
        setState { reducer(Loading) }

        try {
            val result = this@catchAsState.await()
            setState { reducer(Success(result)) }
        } catch (error: Throwable) {
            setState { reducer(Fail(error)) }
        }
    }

    protected inline fun <reified P> SavedStateHandle.selectSaveAsJson(
        property: KProperty1<S, P>
    ) {
        selectSubscribe(property) {
            this[property.name] = Json.encodeToString(it)
        }
    }

    companion object {
        inline fun <S, reified P> SavedStateHandle.selectGetFromJson(
            property: KProperty1<S, P>
        ): P? = get<String>(property.name)?.let {
            Json { coerceInputValues = true }.decodeFromString(it)
        }
    }
}
