package io.github.anvell.keemobile.presentation.explore

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import io.github.anvell.keemobile.common.extensions.append
import io.github.anvell.keemobile.common.extensions.getArguments
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.AppSettings
import io.github.anvell.keemobile.domain.entity.Uninitialized
import io.github.anvell.keemobile.domain.usecase.GetAppSettings
import io.github.anvell.keemobile.domain.usecase.GetFilteredEntries
import io.github.anvell.keemobile.domain.usecase.GetOpenDatabase
import io.github.anvell.keemobile.domain.usecase.SaveAppSettings
import io.github.anvell.keemobile.presentation.base.MviViewModel
import timber.log.Timber
import java.util.*

class ExploreViewModel @ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
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
        getAppSettings
            .use()
            .onErrorReturnItem(AppSettings())
            .doOnError(Timber::d)
            .execute {
                copy(appSettings = it)
            }
    }

    fun updateAppSettings(settings: AppSettings) {
        saveAppSettings
            .use(settings)
            .doOnError(Timber::d)
            .execute {
                copy(appSettings = it)
            }
    }

    fun activateDatabase(id: VaultId) {
        getOpenDatabase
            .use(id)
            .doOnError(Timber::d)
            .execute {
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
            if(state.searchResults is Uninitialized || state.searchResults()?.filter != filter) {
                getFilteredEntries
                    .use(state.activeDatabaseId, filter)
                    .execute {
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
