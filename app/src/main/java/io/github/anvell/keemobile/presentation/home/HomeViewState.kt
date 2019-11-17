package io.github.anvell.keemobile.presentation.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.OpenDatabase

data class HomeViewState(
    val activeDatabaseId: Async<VaultId> = Uninitialized,
    val openDatabases: List<OpenDatabase> = listOf()
) : MvRxState
