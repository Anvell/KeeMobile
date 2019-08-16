package io.github.anvell.keemobile.data.repository

import de.slackspace.openkeepass.KeePassDatabase
import io.github.anvell.keemobile.data.transformer.KeePassTransformer
import io.github.anvell.keemobile.domain.entity.*
import io.github.anvell.keemobile.domain.repository.DatabaseRepository
import io.github.anvell.keemobile.common.io.StorageFile
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.HashMap

@Singleton
class DatabaseRepositoryImpl @Inject constructor(private val storageFile: StorageFile) : DatabaseRepository {

    private val openDatabases = HashMap<UUID, OpenDatabase>()

    override fun readFromSource(source: FileSource, secrets: FileSecrets): UUID {
        val database = when (source) {
            is FileSource.Storage -> readFromStorage(source, secrets)
        }
        return pushDatabase(database, source, secrets)
    }

    protected fun readFromStorage(source: FileSource.Storage, secrets: FileSecrets): KeyDatabase {
        val database = KeePassDatabase
            .getInstance(storageFile.readFromUri(source.uri))
            .openDatabase(secrets.masterKey)

        return KeePassTransformer.from(database)
    }

    override fun createDatabase(source: FileSource, secrets: FileSecrets): UUID {
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
                        password = "PHOENIX"
                    ),
                    KeyEntry(
                        title = "Second entry",
                        password = "ARIADNE"
                    )
                )
            )
        )
        return pushDatabase(sample, source, secrets)
    }

    protected fun pushDatabase(database: KeyDatabase, source: FileSource, secrets: FileSecrets): UUID {
        val id = UUID.randomUUID()
        openDatabases[id] = OpenDatabase(id, database, source, secrets)
        return id
    }
}
