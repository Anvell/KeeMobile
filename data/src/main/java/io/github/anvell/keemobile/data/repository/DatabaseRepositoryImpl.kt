package io.github.anvell.keemobile.data.repository

import de.slackspace.openkeepass.KeePassDatabase
import io.github.anvell.either.*
import io.github.anvell.keemobile.core.io.StorageFile
import io.github.anvell.keemobile.data.transformer.KeePassTransformer
import io.github.anvell.keemobile.domain.entity.*
import io.github.anvell.keemobile.domain.entity.VaultId
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
    private val openDatabases = MutableStateFlow(listOf<OpenDatabase>())

    override val openDatabasesAsFlow: StateFlow<List<OpenDatabase>> = openDatabases

    override fun getOpenDatabaseById(
        id: VaultId
    ): Either<Exception, OpenDatabase> {
        return openDatabases.value
            .find { it.id == id }
            ?.let { Right(it) }
            ?: Left(DatabaseNotOpenException())
    }

    override fun getFilteredEntries(
        id: VaultId,
        filter: String
    ): Either<Exception, List<SearchResult>> {
        return openDatabases.value
            .find { it.id == id }
            ?.let { Right(it.database.filterEntries(filter)) }
            ?: Left(DatabaseNotOpenException())
    }

    override fun close(id: VaultId): Either<Exception, List<OpenDatabase>> {
        val database = openDatabases.value.find { it.id == id }

        return if (database != null) {
            openDatabases.value -= database
            Right(openDatabases.value)
        } else {
            Left(DatabaseNotOpenException())
        }
    }

    override fun closeAll() {
        openDatabases.value = listOf()
    }

    override fun readFromSource(
        source: FileSource,
        secrets: FileSecrets
    ): Either<Exception, OpenDatabase> {
        val alreadyOpen = openDatabases.value.find { it.id == VaultId(source.id) }

        return if (alreadyOpen == null) {
            when (source) {
                is FileSource.Storage -> readFromStorage(source, secrets)
            }.map { database ->
                OpenDatabase(database, source, secrets).also {
                    openDatabases.value += it
                }
            }
        } else {
            Left(DatabaseAlreadyOpenException())
        }
    }

    override fun createDatabase(
        source: FileSource,
        secrets: FileSecrets
    ): Either<Exception, VaultId> = when (source) {
        is FileSource.Storage -> writeToStorage(SampleDatabase, source, secrets)
    }.map { database ->
        VaultId(source.id).also {
            openDatabases.value += OpenDatabase(database, source, secrets)
        }
    }

    private fun readFromStorage(
        source: FileSource.Storage,
        secrets: FileSecrets
    ): Either<Exception, KeyDatabase> = eitherCatch {
        val database = KeePassDatabase
            .getInstance(storageFile.openInputStream(source.uri))
            .run {
                when (secrets) {
                    is KeyOnly -> {
                        require(secrets.masterKey is Secret.Unencrypted) {
                            "Master key must be unencrypted at this point."
                        }
                        openDatabase(
                            (secrets.masterKey as Secret.Unencrypted).content
                        )
                    }
                    is KeyFileOnly -> {
                        require(secrets.keyFile is FileSource.Storage) {
                            "Only 'storage' is supported as file source."
                        }
                        openDatabase(
                            storageFile.openInputStream((secrets.keyFile as FileSource.Storage).uri)
                        )
                    }
                    is KeyWithKeyFile -> {
                        require(secrets.masterKey is Secret.Unencrypted) {
                            "Master key must be unencrypted at this point."
                        }
                        require(secrets.keyFile is FileSource.Storage) {
                            "Only 'storage' is supported as file source."
                        }
                        openDatabase(
                            (secrets.masterKey as Secret.Unencrypted).content,
                            storageFile.openInputStream((secrets.keyFile as FileSource.Storage).uri)
                        )
                    }
                }
            }

        KeePassTransformer.from(database)
    }

    private fun writeToStorage(
        database: KeyDatabase,
        source: FileSource.Storage,
        secrets: FileSecrets
    ) = eitherCatch {
        require(secrets is KeyOnly) {
            "Only password protection is supported."
        }
        require(secrets.masterKey is Secret.Unencrypted) {
            "Master key must be unencrypted at this point."
        }

        storageFile.openOutputStream(source.uri).use { outputStream ->
            database.also {
                KeePassDatabase.write(
                    KeePassTransformer.to(it),
                    (secrets.masterKey as Secret.Unencrypted).content,
                    outputStream
                )
            }
        }
    }
}
