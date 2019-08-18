package io.github.anvell.keemobile.presentation.base

import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState
import io.github.anvell.keemobile.BuildConfig
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel<S : MvRxState>(initialState: S) :
    BaseMvRxViewModel<S>(initialState, debugMode = BuildConfig.DEBUG) {
    protected val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
