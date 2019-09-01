package io.github.anvell.keemobile.presentation.explore

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.OpenDatabase
import java.util.*

data class ExploreViewState(
    val activeDatabaseId: VaultId,
    val activeRoot: UUID? = null,
    val activeDatabase: Async<OpenDatabase> = Uninitialized
) : MvRxState {
    constructor(args: ExploreArgs) : this(args.databaseId, args.rootId)
}
