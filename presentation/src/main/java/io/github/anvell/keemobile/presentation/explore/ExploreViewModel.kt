package io.github.anvell.keemobile.presentation.explore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.anvell.async.Success
import io.github.anvell.async.Uninitialized
import io.github.anvell.either.Right
import io.github.anvell.either.or
import io.github.anvell.keemobile.core.extensions.getJsonArguments
import io.github.anvell.keemobile.core.ui.mvi.MviComposeViewModel
import io.github.anvell.keemobile.domain.entity.*
import io.github.anvell.keemobile.domain.entity.VaultId
import io.github.anvell.keemobile.domain.observers.OpenDatabasesObserver
import io.github.anvell.keemobile.domain.observers.RecentFilesObserver
import io.github.anvell.keemobile.domain.usecase.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

private const val FilterTriggerTimeoutMs = 300L

@OptIn(FlowPreview::class)
@HiltViewModel
class ExploreViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val openDatabasesObserver: OpenDatabasesObserver,
    private val recentFilesObserver: RecentFilesObserver,
    private val getOpenDatabase: GetOpenDatabase,
    private val closeDatabase: CloseDatabase,
    private val closeAllDatabases: CloseAllDatabases,
    private val getFilteredEntries: GetFilteredEntries,
    private val getAppSettings: GetAppSettings,
    private val saveAppSettings: SaveAppSettings,
    private val updateListFileEntry: UpdateListFileEntry
) : MviComposeViewModel<ExploreViewState, ExploreCommand>(
    ExploreViewState(activeDatabaseId = savedStateHandle.getJsonArguments())
) {

    init {
        viewModelScope.launch {
            openDatabasesObserver().collectAsState { copy(databases = it) }
        }
        viewModelScope.launch {
            recentFilesObserver().collectAsState { copy(recentFiles = it) }
        }

        viewModelScope.async {
            getAppSettings().or { Right(AppSettings()) }
        }.reduceAsState {
            copy(appSettings = it)
        }

        withState { state ->
            if (state.activeDatabase is Uninitialized) {
                activateDatabase(state.activeDatabaseId)
            }
        }

        selectSubscribe(ExploreViewState::appSettings) { appSettings ->
            if (appSettings is Success) {
                setState { copy(viewMode = appSettings().exploreViewMode) }
            }
        }
        subscribeToSearchFilter()
    }

    override fun onCommand(command: ExploreCommand) {
        when (command) {
            is ExploreCommand.SetActiveDatabase -> activateDatabase(command.value)
            is ExploreCommand.ChangeViewMode -> changeViewMode(command.value)
            is ExploreCommand.NavigateToRoot -> navigateToRoot()
            is ExploreCommand.NavigateUp -> navigateUp()
            is ExploreCommand.NavigateToGroup -> navigateToGroup(command.id)
            is ExploreCommand.UpdateFilter -> updateFilter(command.value)
            is ExploreCommand.CloseDatabase -> closeDatabaseById(command.value)
            is ExploreCommand.CloseAllFiles -> closeAllFiles()
            is ExploreCommand.SetEncryptedSecrets -> setEncryptedSecrets(
                command.source, command.encryptedSecrets
            )
        }
    }

    private fun subscribeToSearchFilter() = viewModelScope.launch {
        selectAsFlow(ExploreViewState::searchFilter)
            .debounce(FilterTriggerTimeoutMs)
            .collect {
                if (it.isNotBlank()) {
                    filterEntries(it)
                } else {
                    clearFilter()
                }
            }
    }

    private fun changeViewMode(mode: ViewMode) = withState { state ->
        state.appSettings()?.run {
            updateAppSettings(copy(exploreViewMode = mode))
        }
    }

    private fun setEncryptedSecrets(
        fileSource: FileSource,
        encryptedSecrets: FileListEntrySecrets
    ) {
        viewModelScope.launch {
            updateListFileEntry(
                FileListEntry(
                    vault = fileSource,
                    encryptedSecrets = encryptedSecrets
                )
            )
        }
    }

    private fun updateAppSettings(settings: AppSettings) {
        viewModelScope.async {
            saveAppSettings(settings)
        }.reduceAsState {
            copy(appSettings = it)
        }
    }

    private fun activateDatabase(id: VaultId) {
        viewModelScope.async {
            getOpenDatabase(id)
        }.reduceAsState {
            copy(
                activeDatabaseId = id,
                activeDatabase = it,
                navigationStack = listOf(),
                searchResults = Uninitialized
            )
        }
    }

    private fun filterEntries(filter: String) = withState { state ->
        if (state.searchResults is Uninitialized || state.searchResults()?.filter != filter) {
            viewModelScope.async {
                getFilteredEntries(state.activeDatabaseId, filter)
            }.reduceAsState {
                copy(searchResults = it)
            }
        }
    }

    private fun updateFilter(value: String) = setState { copy(searchFilter = value) }

    private fun clearFilter() = setState { copy(searchResults = Uninitialized) }

    private fun navigateToRoot() = setState { copy(navigationStack = listOf()) }

    private fun navigateUp() = setState { copy(navigationStack = navigationStack.dropLast(1)) }

    private fun navigateToGroup(id: UUID) = withState { state ->
        setState {
            copy(navigationStack = state.navigationStack + id)
        }
    }

    private fun closeDatabaseById(id: VaultId) = viewModelScope.launch { closeDatabase(id) }

    private fun closeAllFiles() = viewModelScope.launch { closeAllDatabases() }
}
