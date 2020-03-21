package io.github.anvell.keemobile.presentation.explore

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.PersistState
import com.airbnb.mvrx.Uninitialized
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.AppSettings
import io.github.anvell.keemobile.domain.entity.OpenDatabase
import io.github.anvell.keemobile.domain.entity.SearchResults
import java.util.*

data class ExploreViewState(
    val activeDatabaseId: VaultId,
    @PersistState val rootStack: List<UUID> = listOf(),
    val activeDatabase: Async<OpenDatabase> = Uninitialized,
    val searchResults: Async<SearchResults> = Uninitialized,
    val appSettings: Async<AppSettings> = Uninitialized
) : MvRxState {
    constructor(args: ExploreArgs) : this(args.databaseId)
}
