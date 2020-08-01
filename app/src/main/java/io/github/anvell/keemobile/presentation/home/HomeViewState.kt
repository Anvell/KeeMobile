package io.github.anvell.keemobile.presentation.home

import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.datatypes.Async
import io.github.anvell.keemobile.domain.entity.OpenDatabase
import io.github.anvell.keemobile.domain.datatypes.Uninitialized

data class HomeViewState(
    val activeDatabaseId: Async<VaultId> = Uninitialized,
    val openDatabases: List<OpenDatabase> = listOf()
)
