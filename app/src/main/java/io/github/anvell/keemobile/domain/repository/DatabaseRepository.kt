package io.github.anvell.keemobile.domain.repository

import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.*

interface DatabaseRepository {

    fun getOpenDatabaseById(id: VaultId) : OpenDatabase

    fun getFilteredEntries(id: VaultId, filter: String): List<SearchResult>

    fun readFromSource(source: FileSource, secrets: FileSecrets): VaultId

    fun createDatabase(source: FileSource, secrets: FileSecrets): VaultId
}
