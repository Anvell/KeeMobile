package io.github.anvell.keemobile.presentation.explore

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import com.google.accompanist.insets.statusBarsPadding
import io.github.anvell.async.Success
import io.github.anvell.async.Uninitialized
import io.github.anvell.keemobile.core.ui.components.Dialog
import io.github.anvell.keemobile.core.ui.components.Spacers
import io.github.anvell.keemobile.core.ui.extensions.toast
import io.github.anvell.keemobile.core.ui.locals.LocalAppNavigator
import io.github.anvell.keemobile.core.ui.navigation.AppNavigator
import io.github.anvell.keemobile.core.ui.theme.AppTheme
import io.github.anvell.keemobile.domain.entity.VaultId
import io.github.anvell.keemobile.domain.entity.ViewMode
import io.github.anvell.keemobile.presentation.R
import io.github.anvell.keemobile.presentation.data.EntryType
import io.github.anvell.keemobile.presentation.entrydetails.data.EntryDetailsArgs
import io.github.anvell.keemobile.presentation.explore.components.GroupAsList
import io.github.anvell.keemobile.presentation.explore.components.SearchResultsAsList
import io.github.anvell.keemobile.presentation.explore.components.SearchTextField
import io.github.anvell.keemobile.presentation.explore.components.menu.ExploreMenu
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Explore(
    state: ExploreViewState,
    commands: (ExploreCommand) -> Unit
) {
    val context = LocalContext.current
    val navigator = LocalAppNavigator.current
    var showExploreMenu by rememberSaveable { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .width(dimensionResource(R.dimen.layout_maximum_width))
                .padding(top = dimensionResource(R.dimen.layout_vertical_margin))
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
                            navigator.navigateToDetails(
                                databaseId = state.activeDatabaseId,
                                entryId = it.uuid.toString()
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
                                    navigator.navigateToDetails(
                                        databaseId = state.activeDatabaseId,
                                        entryId = it.uuid.toString()
                                    )
                                }
                            )
                        }
                        ViewMode.LIST -> {
                            SearchResultsAsList(
                                results = remember(database) { database.findEntries { true } },
                                onEntryClicked = {
                                    navigator.navigateToDetails(
                                        databaseId = state.activeDatabaseId,
                                        entryId = it.uuid.toString()
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showExploreMenu && state.databases is Success && state.recentFiles is Success) {
        AppTheme.Dialog(
            onDismissRequest = { showExploreMenu = false },
            backgroundColor = Color.Transparent,
            buttons = {
                ExploreMenu(
                    selected = state.databases
                        .unwrap()
                        .first { it.id == state.activeDatabaseId },
                    selectedEncryptedSecrets = state.recentFiles
                        .unwrap()
                        .first { VaultId(it.fileSource.id) == state.activeDatabaseId }
                        .encryptedSecrets,
                    items = state.databases.unwrap(),
                    onItemSelected = {
                        commands(ExploreCommand.SetActiveDatabase(it))
                        showExploreMenu = false
                    },
                    onCloseItem = {
                        commands(ExploreCommand.CloseDatabase(it))
                    },
                    onOpen = {
                        navigator.navigate(id = R.id.action_open_database)
                        showExploreMenu = false
                    },
                    onUseBiometrics = { source, encryptedSecrets ->
                        commands(ExploreCommand.SetEncryptedSecrets(source, encryptedSecrets))
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

private fun AppNavigator.navigateToDetails(
    databaseId: VaultId,
    entryId: String
) {
    navigate(
        R.id.action_entry_details,
        Json.encodeToString(
            EntryDetailsArgs(
                databaseId = databaseId,
                entryType = EntryType.Actual(entryId)
            )
        )
    )
}
