package io.github.anvell.keemobile.data.repository

import de.slackspace.openkeepass.KeePassDatabase
import io.github.anvell.keemobile.common.io.StorageFile
import io.github.anvell.keemobile.data.transformer.KeePassTransformer
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.*
import io.github.anvell.keemobile.domain.exceptions.DatabaseAlreadyOpenException
import io.github.anvell.keemobile.domain.exceptions.DatabaseNotOpenException
import io.github.anvell.keemobile.domain.repository.DatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseRepositoryImpl @Inject constructor(
    private val storageFile: StorageFile
) : DatabaseRepository {
    private val openDatabasesRelay = MutableStateFlow(listOf<OpenDatabase>())
    private var openDatabases = listOf<OpenDatabase>()
        private set(value) {
            field = value
            openDatabasesRelay.value = value
        }

    override fun getOpenDatabases(): StateFlow<List<OpenDatabase>> {
        return openDatabasesRelay
    }

    override fun getOpenDatabaseById(id: VaultId): OpenDatabase {
        val database = openDatabases.find { it.id == id }

        if (database != null) {
            return database
        } else {
            throw DatabaseNotOpenException()
        }
    }

    override fun getFilteredEntries(id: VaultId, filter: String): List<SearchResult> {
        val database = openDatabases.find { it.id == id }

        if (database != null) {
            return database.database.filterEntries(filter)
        } else {
            throw DatabaseNotOpenException()
        }
    }

    override fun close(id: VaultId): List<OpenDatabase> {
        val database = openDatabases.find { it.id == id }

        if (database != null) {
            openDatabases = openDatabases.filter { it.id != id }
            return openDatabases
        } else {
            throw DatabaseNotOpenException()
        }
    }

    override fun closeAll() {
        openDatabases = listOf()
    }

    override fun readFromSource(source: FileSource, secrets: FileSecrets): VaultId {
        val alreadyOpen = openDatabases.find { it.id == source.id }

        if (alreadyOpen == null) {
            val database = when (source) {
                is FileSource.Storage -> readFromStorage(source, secrets)
            }
            return pushDatabase(database, source, secrets)
        } else {
            throw DatabaseAlreadyOpenException()
        }
    }

    override fun createDatabase(source: FileSource, secrets: FileSecrets): VaultId {
        val sample = KeyDatabase(
            KeyMeta(),
            KeyGroup(
                name = "MyDB",
                notes = "Some notes",
                groups = mutableListOf(
                    KeyGroup(name = "Special", notes = "Lorem ipsum dolor")
                ), entries = mutableListOf(
                    KeyEntry(
                        title = "My entry",
                        password = "GJKHEFJEH656"
                    ),
                    KeyEntry(
                        title = "Second entry",
                        password = "R#JFJDERFLL"
                    )
                )
            )
        )

        when (source) {
            is FileSource.Storage -> writeToStorage(sample, source, secrets)
        }

        return pushDatabase(sample, source, secrets)
    }

    private fun readFromStorage(source: FileSource.Storage, secrets: FileSecrets): KeyDatabase {
        val database = KeePassDatabase
            .getInstance(storageFile.openInputStream(source.uri))
            .openDatabase(secrets.masterKey)

        return KeePassTransformer.from(database)
    }

    private fun writeToStorage(database: KeyDatabase, source: FileSource.Storage, secrets: FileSecrets) {
        val outputStream = storageFile.openOutputStream(source.uri)
        KeePassDatabase.write(KeePassTransformer.to(database), secrets.masterKey, outputStream)
    }

    private fun pushDatabase(database: KeyDatabase, source: FileSource, secrets: FileSecrets): VaultId {
        openDatabases = openDatabases + OpenDatabase(database, source, secrets)
        return source.id
    }
}
