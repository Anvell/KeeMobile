package io.github.anvell.keemobile.data.repository

import dagger.Reusable
import io.github.anvell.keemobile.core.constants.AppConstants
import io.github.anvell.keemobile.core.io.InternalFile
import io.github.anvell.keemobile.core.io.StorageFile
import io.github.anvell.keemobile.core.security.KeystoreEncryption
import io.github.anvell.keemobile.domain.datatypes.Either
import io.github.anvell.keemobile.domain.datatypes.eitherCatch
import io.github.anvell.keemobile.domain.entity.FileListEntry
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.domain.repository.RecentFilesRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject

@Reusable
class RecentFilesRepositoryImpl @Inject constructor(
    private val internalFile: InternalFile,
    private val storageFile: StorageFile,
    private val keystoreEncryption: KeystoreEncryption,
) : RecentFilesRepository {
    private val recentFiles = MutableSharedFlow<List<FileListEntry>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val recentFilesAsFlow: SharedFlow<List<FileListEntry>> = recentFiles

    override suspend fun readRecentFiles(): Either<Exception, List<FileListEntry>> = eitherCatch {
        if (!internalFile.exists(AppConstants.FILE_RECENT_FILES)) {
            throw IOException("${AppConstants.FILE_RECENT_FILES} does not exist.")
        }

        internalFile.openInputStream(AppConstants.FILE_RECENT_FILES)?.use { stream ->
            val data = keystoreEncryption.decrypt(
                AppConstants.KEYSTORE_ALIAS_RECENT_FILES,
                stream.readBytes(),
                AppConstants.FILE_RECENT_FILES.toByteArray()
            ).toString(Charsets.UTF_8)

            Json.decodeFromString<List<FileListEntry>>(data).filter {
                when (val source = it.fileSource) {
                    is FileSource.Storage -> {
                        storageFile.checkUriPermission(source.uri) && storageFile.exists(source.uri)
                    }
                    else -> true
                }
            }.also {
                recentFiles.emit(it)
            }
        } ?: throw IOException("Cannot open ${AppConstants.FILE_RECENT_FILES}")
    }

    override suspend fun writeRecentFiles(
        items: List<FileListEntry>
    ): Either<Exception, List<FileListEntry>> = eitherCatch {
        internalFile.openOutputStream(AppConstants.FILE_RECENT_FILES)?.use { stream ->
            val data = Json.encodeToString(items)
            val encrypted = keystoreEncryption.encrypt(
                AppConstants.KEYSTORE_ALIAS_RECENT_FILES,
                data.toByteArray(),
                AppConstants.FILE_RECENT_FILES.toByteArray()
            )
            stream.write(encrypted)
            items.also {
                recentFiles.emit(it)
            }
        } ?: throw IOException("Cannot write ${AppConstants.FILE_RECENT_FILES}")
    }

    override suspend fun clearRecentFiles(): Either<Exception, Unit> = eitherCatch {
        if (internalFile.exists(AppConstants.FILE_RECENT_FILES)) {
            internalFile.remove(AppConstants.FILE_RECENT_FILES)
        }
        recentFiles.emit(listOf())
    }
}
