package io.github.anvell.keemobile.presentation.explore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.anvell.keemobile.core.extensions.getArguments
import io.github.anvell.keemobile.core.ui.mvi.MviComposeViewModel
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.datatypes.Right
import io.github.anvell.keemobile.domain.datatypes.Success
import io.github.anvell.keemobile.domain.datatypes.Uninitialized
import io.github.anvell.keemobile.domain.datatypes.or
import io.github.anvell.keemobile.domain.entity.*
import io.github.anvell.keemobile.domain.observers.OpenDatabasesObserver
import io.github.anvell.keemobile.domain.usecase.*
import kotlinx.coroutines.FlowPreview
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
    private val getOpenDatabase: GetOpenDatabase,
    private val closeDatabase: CloseDatabase,
    private val closeAllDatabases: CloseAllDatabases,
    private val getFilteredEntries: GetFilteredEntries,
    private val getAppSettings: GetAppSettings,
    private val saveAppSettings: SaveAppSettings,
    private val updateListFileEntry: UpdateListFileEntry
) : MviComposeViewModel<ExploreViewState, ExploreCommand>(
    ExploreViewState(activeDatabaseId = savedStateHandle.getArguments())
) {

    init {
        viewModelScope.launch {
            openDatabasesObserver().executeCatching { copy(databases = it) }
        }

        execute({
            getAppSettings().or { Right(AppSettings()) }
        }) {
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
                command.source, command.secrets
            )
        }
    }

    private fun subscribeToSearchFilter() = viewModelScope.launch {
        selectSubscribe(ExploreViewState::searchFilter)
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

    private fun setEncryptedSecrets(fileSource: FileSource, fileSecrets: FileSecrets) {
        viewModelScope.launch {
            updateListFileEntry(
                FileListEntry(
                    fileSource = fileSource,
                    encryptedSecrets = FileListEntrySecrets.Some(fileSecrets)
                )
            )
        }
    }

    private fun updateAppSettings(settings: AppSettings) {
        execute({ saveAppSettings(settings) }) {
            copy(appSettings = it)
        }
    }

    private fun activateDatabase(id: VaultId) {
        execute({ getOpenDatabase(id) }) {
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
            execute({
                getFilteredEntries(state.activeDatabaseId, filter)
            }) {
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
