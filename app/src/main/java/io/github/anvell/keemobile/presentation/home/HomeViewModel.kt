package io.github.anvell.keemobile.presentation.home

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.anvell.keemobile.presentation.base.BaseViewModel

class HomeViewModel @AssistedInject constructor(@Assisted initialState: HomeViewState) :
    BaseViewModel<HomeViewState>(initialState) {

    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: HomeViewState): HomeViewModel
    }

    companion object : MvRxViewModelFactory<HomeViewModel, HomeViewState> {

        override fun create(viewModelContext: ViewModelContext, state: HomeViewState): HomeViewModel? {
            val activity = (viewModelContext as ActivityViewModelContext).activity<HomeActivity>()
            return activity.viewModelFactory.create(state)
        }
    }
}
