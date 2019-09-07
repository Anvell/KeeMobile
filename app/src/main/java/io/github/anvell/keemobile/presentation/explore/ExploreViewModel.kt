package io.github.anvell.keemobile.presentation.explore

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.anvell.keemobile.common.extensions.append
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.usecase.GetOpenDatabase
import io.github.anvell.keemobile.presentation.base.BaseViewModel
import java.util.*

class ExploreViewModel @AssistedInject constructor(
    @Assisted initialState: ExploreViewState,
    private val getOpenDatabase: GetOpenDatabase
) : BaseViewModel<ExploreViewState>(initialState) {

    init {
        withState { state ->
            if(state.activeDatabase is Uninitialized) {
                activateDatabase(state.activeDatabaseId)
            }
        }
    }

    fun activateDatabase(id: VaultId) {
        getOpenDatabase
            .use(id)
            .execute {
                copy(activeDatabase = it)
            }
    }

    fun activateGroup(id: UUID) {
        setState {
            copy(rootStack = rootStack.append(id))
        }
    }

    fun navigateUp() {
        setState {
            copy(rootStack = rootStack.dropLast(1))
        }
    }

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
