package io.github.anvell.keemobile.domain.repository

import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.FileSecrets
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.domain.entity.OpenDatabase
import io.github.anvell.keemobile.domain.entity.SearchResult
import kotlinx.coroutines.flow.StateFlow

interface DatabaseRepository {

    fun getOpenDatabases(): StateFlow<List<OpenDatabase>>

    fun getOpenDatabaseById(id: VaultId) : OpenDatabase

    fun getFilteredEntries(id: VaultId, filter: String): List<SearchResult>

    fun close(id: VaultId): List<OpenDatabase>

    fun readFromSource(source: FileSource, secrets: FileSecrets): VaultId

    fun createDatabase(source: FileSource, secrets: FileSecrets): VaultId

    fun closeAll()
}
