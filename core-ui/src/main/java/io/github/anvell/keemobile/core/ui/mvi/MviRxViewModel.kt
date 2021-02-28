package io.github.anvell.keemobile.core.ui.mvi

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import io.github.anvell.keemobile.domain.datatypes.Async
import io.github.anvell.keemobile.domain.datatypes.Fail
import io.github.anvell.keemobile.domain.datatypes.Loading
import io.github.anvell.keemobile.domain.datatypes.Success
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty1

abstract class MviRxViewModel<S>(initialState: S) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val state = MutableStateFlow(initialState)

    fun observableState() = state.asLiveData()

    fun <T> withState(block: (S) -> T) = block(state.value)

    protected fun setState(reducer: S.() -> S) {
        state.value = reducer(state.value)
    }

    protected fun stateSubscribe(block: (S) -> Unit) {
        viewModelScope.launch {
            state.collect { block(it) }
        }
    }

    fun <P> selectSubscribe(property: KProperty1<S, P>): LiveData<P> {
        return selectSubscribeInternal(property).asLiveData()
    }

    protected fun <P> selectSubscribe(property: KProperty1<S, P>, block: (P) -> Unit) {
        viewModelScope.launch {
            selectSubscribeInternal(property).collect { block(it) }
        }
    }

    private fun <P> selectSubscribeInternal(property: KProperty1<S, P>): Flow<P> {
        return state.map { property.get(it) }.distinctUntilChanged()
    }

    protected fun <V> executeSync(block: () -> V, reducer: S.(Async<V>) -> S) {
        try {
            val result = block()
            setState { reducer(Success(result)) }
        } catch (error: Throwable) {
            setState { reducer(Fail(error)) }
        }
    }

    protected fun <V> execute(block: suspend () -> V, reducer: S.(Async<V>) -> S) {
        viewModelScope.launch {
            setState { reducer(Loading) }

            try {
                val result = block()
                setState { reducer(Success(result)) }
            } catch (error: Throwable) {
                setState { reducer(Fail(error)) }
            }
        }
    }

    protected suspend fun <T> Flow<T>.execute(reducer: S.(T) -> S) = collect { setState { reducer(it) } }

    protected fun Completable.execute(reducer: S.(Async<Unit>) -> S) = toSingle { Unit }.execute(reducer)

    protected fun <T> Single<T>.execute(reducer: S.(Async<T>) -> S) = toObservable().execute({ it }, reducer)

    protected fun <T> Observable<T>.execute(reducer: S.(Async<T>) -> S) = execute({ it }, reducer)

    protected fun <T, V> Observable<T>.execute(
        mapper: (T) -> V,
        reducer: S.(Async<V>) -> S
    ): Disposable {
        setState { reducer(Loading) }

        return map<Async<V>> { Success(mapper(it)) }
            .onErrorReturn { Fail(it) }
            .subscribe { setState { reducer(it) } }
            .disposeOnClear()
    }

    protected fun Disposable.disposeOnClear(): Disposable {
        disposables.add(this)
        return this
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}
