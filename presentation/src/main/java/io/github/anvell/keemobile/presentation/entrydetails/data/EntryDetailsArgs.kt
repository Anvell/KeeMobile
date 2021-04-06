package io.github.anvell.keemobile.presentation.entrydetails.data

import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.presentation.data.EntryType
import kotlinx.serialization.Serializable

@Serializable
data class EntryDetailsArgs(
    val databaseId: VaultId,
    val entryType: EntryType
)
