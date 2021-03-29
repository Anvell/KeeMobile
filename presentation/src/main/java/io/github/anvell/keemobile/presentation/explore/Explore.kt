package io.github.anvell.keemobile.presentation.explore

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import io.github.anvell.keemobile.core.ui.components.Dialog
import io.github.anvell.keemobile.core.ui.components.Spacers
import io.github.anvell.keemobile.core.ui.extensions.toast
import io.github.anvell.keemobile.core.ui.locals.LocalAppNavigator
import io.github.anvell.keemobile.core.ui.theme.AppTheme
import io.github.anvell.keemobile.domain.datatypes.Success
import io.github.anvell.keemobile.domain.datatypes.Uninitialized
import io.github.anvell.keemobile.domain.entity.ViewMode
import io.github.anvell.keemobile.presentation.R
import io.github.anvell.keemobile.presentation.entry.EntryDetailsArgs
import io.github.anvell.keemobile.presentation.explore.components.GroupAsList
import io.github.anvell.keemobile.presentation.explore.components.SearchResultsAsList
import io.github.anvell.keemobile.presentation.explore.components.SearchTextField
import io.github.anvell.keemobile.presentation.explore.components.menu.ExploreMenu

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Explore(
    state: ExploreViewState,
    commands: (ExploreCommand) -> Unit
) {
    val context = LocalContext.current
    val navigator = LocalAppNavigator.current
    var showExploreMenu by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = dimensionResource(R.dimen.layout_vertical_margin))
    ) {
        SearchTextField(
            value = state.searchFilter,
            viewMode = state.viewMode,
            leading = {
                AnimatedVisibility(
                    visible = state.searchFilter.isEmpty() &&
                            state.navigationStack.isNotEmpty() &&
                            state.viewMode == ViewMode.TREE,
                    enter = expandHorizontally(),
                    exit = shrinkHorizontally(),
                ) {
                    IconButton(onClick = { commands(ExploreCommand.NavigateUp) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onSurface
                        )
                    }
                }
                if (state.searchFilter.isNotEmpty() ||
                    state.navigationStack.isEmpty() ||
                    state.viewMode != ViewMode.TREE
                ) {
                    Spacers.S()
                }
            },
            onValueChange = { commands(ExploreCommand.UpdateFilter(it)) },
            onViewModeChange = { commands(ExploreCommand.ChangeViewMode(it)) },
            onMoreClicked = { showExploreMenu = true },
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.layout_horizontal_margin)
            )
        )
        Spacers.M()

        when {
            state.searchResults is Success -> {
                SearchResultsAsList(
                    results = state.searchResults.unwrap().filteredEntries,
                    onEntryClicked = {
                        navigator.navigate(
                            R.id.action_entry_details,
                            EntryDetailsArgs(state.activeDatabaseId, it.uuid)
                        )
                    }
                )
            }
            state.searchResults is Uninitialized && state.activeDatabase is Success -> {
                val database = state.activeDatabase.unwrap().database

                when (state.appSettings()?.exploreViewMode) {
                    ViewMode.TREE -> {
                        val group = if (state.navigationStack.isEmpty()) database.root else {
                            database.findGroup { it.uuid == state.navigationStack.last() }
                        }
                        GroupAsList(
                            group = group!!,
                            onGroupClicked = { commands(ExploreCommand.NavigateToGroup(it.uuid)) },
                            onEntryClicked = {
                                navigator.navigate(
                                    R.id.action_entry_details,
                                    EntryDetailsArgs(state.activeDatabaseId, it.uuid)
                                )
                            }
                        )
                    }
                    ViewMode.LIST -> {
                        SearchResultsAsList(
                            results = remember(database) { database.findEntries { true } },
                            onEntryClicked = {
                                navigator.navigate(
                                    R.id.action_entry_details,
                                    EntryDetailsArgs(state.activeDatabaseId, it.uuid)
                                )
                            }
                        )
                    }
                }

            }
        }
    }

    if (showExploreMenu && state.databases is Success) {
        AppTheme.Dialog(
            onDismissRequest = { showExploreMenu = false },
            backgroundColor = Color.Transparent,
            buttons = {
                ExploreMenu(
                    selected = state.databases.unwrap().first {
                        it.id == state.activeDatabaseId
                    },
                    items = state.databases.unwrap(),
                    onItemSelected = {
                        commands(ExploreCommand.SetActiveDatabase(it))
                        showExploreMenu = false
                    },
                    onOpen = {
                        navigator.navigate(id = R.id.action_open_database)
                        showExploreMenu = false
                    },
                    onUseBiometrics = { source, secrets ->
                        commands(ExploreCommand.SetEncryptedSecrets(source, secrets))
                        showExploreMenu = false
                    }
                )
            }
        )
    }

    BackHandler {
        when {
            state.searchResults !is Uninitialized -> {
                commands(ExploreCommand.UpdateFilter(""))
            }
            state.navigationStack.isNotEmpty() -> {
                commands(ExploreCommand.NavigateUp)
            }
            else -> {
                commands(ExploreCommand.CloseAllFiles)
                context.toast(context.getString(R.string.explore_all_files_closed))
                navigator.popBackStack()
            }
        }
    }
}
