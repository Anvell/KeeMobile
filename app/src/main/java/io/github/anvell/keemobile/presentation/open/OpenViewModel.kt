package io.github.anvell.keemobile.presentation.open

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.anvell.keemobile.presentation.core.BaseViewModel

class OpenViewModel @AssistedInject constructor(@Assisted initialState: OpenViewState) :
    BaseViewModel<OpenViewState>(initialState) {

    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: OpenViewState): OpenViewModel
    }

    companion object : MvRxViewModelFactory<OpenViewModel, OpenViewState> {
        override fun create(viewModelContext: ViewModelContext, state: OpenViewState): OpenViewModel? {
            val fragment: OpenFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.viewModelFactory.create(state)
        }
    }
}
