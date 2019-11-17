package io.github.anvell.keemobile.domain.repository

import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.*
import io.reactivex.Observable

interface DatabaseRepository {

    fun getOpenDatabases(): Observable<List<OpenDatabase>>

    fun getOpenDatabaseById(id: VaultId) : OpenDatabase

    fun getFilteredEntries(id: VaultId, filter: String): List<SearchResult>

    fun closeDatabase(id: VaultId): List<OpenDatabase>

    fun readFromSource(source: FileSource, secrets: FileSecrets): VaultId

    fun createDatabase(source: FileSource, secrets: FileSecrets): VaultId
}
