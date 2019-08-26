package io.github.anvell.keemobile.data.repository

import de.slackspace.openkeepass.KeePassDatabase
import io.github.anvell.keemobile.common.io.StorageFile
import io.github.anvell.keemobile.data.transformer.KeePassTransformer
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.*
import io.github.anvell.keemobile.domain.exceptions.DatabaseNotOpenException
import io.github.anvell.keemobile.domain.repository.DatabaseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseRepositoryImpl @Inject constructor(private val storageFile: StorageFile) : DatabaseRepository {

    private val openDatabases = HashMap<VaultId, OpenDatabase>()

    override fun getOpenDatabaseById(id: VaultId): OpenDatabase {
        val database = openDatabases[id]

        if(database != null) {
            return database
        } else {
            throw DatabaseNotOpenException()
        }
    }

    override fun readFromSource(source: FileSource, secrets: FileSecrets): VaultId {
        val database = when (source) {
            is FileSource.Storage -> readFromStorage(source, secrets)
        }
        return pushDatabase(database, source, secrets)
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
        openDatabases[source.id] = OpenDatabase(database, source, secrets)
        return source.id
    }

}
