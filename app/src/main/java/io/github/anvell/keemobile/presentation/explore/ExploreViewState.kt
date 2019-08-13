package io.github.anvell.keemobile.presentation.explore

import com.airbnb.mvrx.MvRxState
import java.util.*

data class ExploreViewState(
    val databaseId: UUID? = null
) : MvRxState {
    constructor(args: ExploreArgs) : this(args.databaseId)
}
