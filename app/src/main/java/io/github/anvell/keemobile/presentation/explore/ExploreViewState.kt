package io.github.anvell.keemobile.presentation.explore

import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.datatypes.Async
import io.github.anvell.keemobile.domain.datatypes.Uninitialized
import io.github.anvell.keemobile.domain.entity.*
import java.util.*

data class ExploreViewState(
    val activeDatabaseId: VaultId,
    val rootStack: List<UUID> = listOf(),
    val activeDatabase: Async<OpenDatabase> = Uninitialized,
    val searchResults: Async<SearchResults> = Uninitialized,
    val appSettings: Async<AppSettings> = Uninitialized
) {
    constructor(args: ExploreArgs) : this(args.databaseId)
}
