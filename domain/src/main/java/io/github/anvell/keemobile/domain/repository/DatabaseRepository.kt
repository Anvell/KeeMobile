package io.github.anvell.keemobile.domain.repository

import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.datatypes.Either
import io.github.anvell.keemobile.domain.entity.FileSecrets
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.domain.entity.OpenDatabase
import io.github.anvell.keemobile.domain.entity.SearchResult
import kotlinx.coroutines.flow.StateFlow

interface DatabaseRepository {
    val openDatabasesAsFlow: StateFlow<List<OpenDatabase>>

    fun getOpenDatabaseById(id: VaultId): Either<Exception, OpenDatabase>

    fun getFilteredEntries(id: VaultId, filter: String): Either<Exception, List<SearchResult>>

    fun close(id: VaultId): Either<Exception, List<OpenDatabase>>

    fun readFromSource(source: FileSource, secrets: FileSecrets): Either<Exception, OpenDatabase>

    fun createDatabase(source: FileSource, secrets: FileSecrets): Either<Exception, VaultId>

    fun closeAll()
}
