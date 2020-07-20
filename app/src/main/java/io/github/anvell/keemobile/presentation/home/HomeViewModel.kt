package io.github.anvell.keemobile.presentation.home

import androidx.hilt.lifecycle.ViewModelInject
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.Uninitialized
import io.github.anvell.keemobile.domain.usecase.CloseAllDatabases
import io.github.anvell.keemobile.domain.usecase.CloseDatabase
import io.github.anvell.keemobile.domain.usecase.GetOpenDatabase
import io.github.anvell.keemobile.domain.usecase.GetOpenDatabases
import io.github.anvell.keemobile.presentation.base.MviViewModel

class HomeViewModel @ViewModelInject constructor(
    private val getOpenDatabase: GetOpenDatabase,
    private val getOpenDatabases: GetOpenDatabases,
    private val closeAllDatabases: CloseAllDatabases,
    private val closeDatabase: CloseDatabase
) : MviViewModel<HomeViewState>(HomeViewState()) {

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

    fun closeDatabase(id: VaultId) = withState { state ->
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
}
