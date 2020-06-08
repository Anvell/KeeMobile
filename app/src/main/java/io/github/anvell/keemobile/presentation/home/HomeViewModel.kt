package io.github.anvell.keemobile.presentation.home

import com.airbnb.mvrx.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.usecase.CloseAllDatabases
import io.github.anvell.keemobile.domain.usecase.CloseDatabase
import io.github.anvell.keemobile.domain.usecase.GetOpenDatabase
import io.github.anvell.keemobile.domain.usecase.GetOpenDatabases
import io.github.anvell.keemobile.presentation.base.BaseViewModel

class HomeViewModel @AssistedInject constructor(
    @Assisted initialState: HomeViewState,
    private val getOpenDatabase: GetOpenDatabase,
    private val getOpenDatabases: GetOpenDatabases,
    private val closeAllDatabases: CloseAllDatabases,
    private val closeDatabase: CloseDatabase
) : BaseViewModel<HomeViewState>(initialState) {

    init {
        getOpenDatabases
            .use()
            .subscribe {
                setState {
                    copy(openDatabases = it)
                }
            }
            .disposeOnClear()
    }

    fun switchDatabase(id: VaultId) {
        getOpenDatabase
            .use(id)
            .map { it.id }
            .execute {
                copy(activeDatabaseId = it)
            }
    }

    fun closeDatabase(id: VaultId) {
        closeDatabase
            .use(id)
            .map { if (it.isNotEmpty()) it.first().id else "" }
            .execute {
                copy(activeDatabaseId = it)
            }
    }

    fun closeAllDatabases() {
        closeAllDatabases
            .use()
            .execute {
                copy(activeDatabaseId = Uninitialized)
            }
    }

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
