package io.github.anvell.keemobile.presentation.explore

import com.airbnb.mvrx.MvRxState
import io.github.anvell.keemobile.domain.alias.VaultId
import java.util.*

data class ExploreViewState(
    val databaseId: VaultId? = null
) : MvRxState {
    constructor(args: ExploreArgs) : this(args.databaseId)
}
