package io.github.anvell.keemobile.presentation.explore

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.anvell.keemobile.core.extensions.getArguments
import io.github.anvell.keemobile.core.ui.extensions.append
import io.github.anvell.keemobile.core.ui.mvi.MviViewModel
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.datatypes.Uninitialized
import io.github.anvell.keemobile.domain.entity.AppSettings
import io.github.anvell.keemobile.domain.usecase.GetAppSettings
import io.github.anvell.keemobile.domain.usecase.GetFilteredEntries
import io.github.anvell.keemobile.domain.usecase.GetOpenDatabase
import io.github.anvell.keemobile.domain.usecase.SaveAppSettings
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getOpenDatabase: GetOpenDatabase,
    private val getFilteredEntries: GetFilteredEntries,
    private val getAppSettings: GetAppSettings,
    private val saveAppSettings: SaveAppSettings
) : MviViewModel<ExploreViewState>(
    ExploreViewState(args = savedStateHandle.getArguments())
) {

    init {
        withState { state ->
            if(state.activeDatabase is Uninitialized) {
                activateDatabase(state.activeDatabaseId)
            }
        }

        loadAppSettings()
    }

    private fun loadAppSettings() {
        execute({
            runCatching {
                getAppSettings()
            }.getOrElse {
                AppSettings()
            }
        }) {
            copy(appSettings = it)
        }
    }

    fun updateAppSettings(settings: AppSettings) {
        execute({ saveAppSettings(settings) }) {
            copy(appSettings = it)
        }
    }

    fun activateDatabase(id: VaultId) {
        execute({ getOpenDatabase(id) }) {
            copy(activeDatabase = it)
        }
    }

    fun activateGroup(id: UUID) {
        setState {
            copy(rootStack = rootStack.append(id))
        }
    }

    fun filterEntries(filter: String) {
        withState { state ->
            if (state.searchResults is Uninitialized || state.searchResults()?.filter != filter) {
                execute({ getFilteredEntries(state.activeDatabaseId, filter) }) {
                    copy(searchResults = it)
                }
            }
        }
    }

    fun clearFilter() {
        setState {
            copy(searchResults = Uninitialized)
        }
    }

    fun resetRoot() {
        setState {
            copy(rootStack = listOf())
        }
    }

    fun navigateUp() {
        setState {
            copy(rootStack = rootStack.dropLast(1))
        }
    }
}
