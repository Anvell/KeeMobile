package io.github.anvell.keemobile.presentation.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import io.github.anvell.keemobile.core.ui.mvi.MviViewModel
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.datatypes.Uninitialized
import io.github.anvell.keemobile.domain.usecase.CloseAllDatabases
import io.github.anvell.keemobile.domain.usecase.CloseDatabase
import io.github.anvell.keemobile.domain.usecase.GetOpenDatabase
import io.github.anvell.keemobile.domain.usecase.GetOpenDatabases
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val getOpenDatabase: GetOpenDatabase,
    private val getOpenDatabases: GetOpenDatabases,
    private val closeAllDatabases: CloseAllDatabases,
    private val closeDatabaseById: CloseDatabase
) : MviViewModel<HomeViewState>(HomeViewState()) {

    init {
        viewModelScope.launch {
            getOpenDatabases().execute {
                copy(openDatabases = it)
            }
        }
    }

    fun switchDatabase(toId: VaultId) {
        execute({ getOpenDatabase(toId).id }) {
            copy(activeDatabaseId = it)
        }
    }

    fun closeDatabase(id: VaultId) {
        execute({
            closeDatabaseById(id).let {
                if (it.isNotEmpty()) it.first().id else ""
            }
        }) {
            copy(activeDatabaseId = it)
        }
    }

    fun closeAllDatabases() = executeSync(closeAllDatabases::invoke) {
        copy(activeDatabaseId = Uninitialized)
    }
}
