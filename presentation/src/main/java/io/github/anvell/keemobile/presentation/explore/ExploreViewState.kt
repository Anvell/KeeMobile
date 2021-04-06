package io.github.anvell.keemobile.presentation.explore

import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.datatypes.Async
import io.github.anvell.keemobile.domain.datatypes.Uninitialized
import io.github.anvell.keemobile.domain.entity.*
import java.util.*

data class ExploreViewState(
    val activeDatabaseId: VaultId,
    val searchFilter: String = "",
    val viewMode: ViewMode = ViewMode.TREE,
    val navigationStack: List<UUID> = listOf(),
    val databases: Async<List<OpenDatabase>> = Uninitialized,
    val recentFiles: Async<List<FileListEntry>> = Uninitialized,
    val activeDatabase: Async<OpenDatabase> = Uninitialized,
    val searchResults: Async<SearchResults> = Uninitialized,
    val appSettings: Async<AppSettings> = Uninitialized
)
