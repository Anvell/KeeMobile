package io.github.anvell.keemobile.presentation.explore

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.anvell.keemobile.domain.usecase.CreateNewFile
import io.github.anvell.keemobile.presentation.core.BaseViewModel

class ExploreViewModel @AssistedInject constructor(
    @Assisted initialState: ExploreViewState
) : BaseViewModel<ExploreViewState>(initialState) {

    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: ExploreViewState): ExploreViewModel
    }

    companion object : MvRxViewModelFactory<ExploreViewModel, ExploreViewState> {
        override fun create(viewModelContext: ViewModelContext, state: ExploreViewState): ExploreViewModel? {
            val fragment: ExploreFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.viewModelFactory.create(state)
        }
    }
}
