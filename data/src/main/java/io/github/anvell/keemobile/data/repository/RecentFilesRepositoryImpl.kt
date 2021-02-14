package io.github.anvell.keemobile.data.repository

import dagger.Reusable
import io.github.anvell.keemobile.core.constants.AppConstants
import io.github.anvell.keemobile.core.io.InternalFile
import io.github.anvell.keemobile.core.io.StorageFile
import io.github.anvell.keemobile.core.security.KeystoreEncryption
import io.github.anvell.keemobile.domain.entity.FileListEntry
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.domain.repository.RecentFilesRepository
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

    override fun readRecentFiles(): List<FileListEntry> {
        if (!internalFile.exists(AppConstants.FILE_RECENT_FILES)) {
            throw RuntimeException("${AppConstants.FILE_RECENT_FILES} does not exist.")
        }

        internalFile.openInputStream(AppConstants.FILE_RECENT_FILES)?.use { stream ->
            val data = keystoreEncryption.decrypt(
                AppConstants.KEYSTORE_ALIAS_RECENT_FILES,
                stream.readBytes(),
                AppConstants.FILE_RECENT_FILES.toByteArray()
            ).toString(Charsets.UTF_8)

            return Json.decodeFromString<List<FileListEntry>>(data).filter {
                when (val source = it.fileSource) {
                    is FileSource.Storage -> {
                        storageFile.checkUriPermission(source.uri) && storageFile.exists(source.uri)
                    }
                    else -> true
                }
            }
        }

        throw IOException("Cannot open ${AppConstants.FILE_RECENT_FILES}")
    }

    override fun writeRecentFiles(recentFiles: List<FileListEntry>): List<FileListEntry> {
        internalFile.openOutputStream(AppConstants.FILE_RECENT_FILES)?.use { stream ->
            val data = Json.encodeToString(recentFiles)

            val encrypted = keystoreEncryption.encrypt(
                AppConstants.KEYSTORE_ALIAS_RECENT_FILES,
                data.toByteArray(),
                AppConstants.FILE_RECENT_FILES.toByteArray()
            )
            stream.write(encrypted)
            return recentFiles
        }

        throw IOException("Cannot write ${AppConstants.FILE_RECENT_FILES}")
    }

    override fun clearRecentFiles(): Boolean {
        return if (internalFile.exists(AppConstants.FILE_RECENT_FILES)) {
            internalFile.remove(AppConstants.FILE_RECENT_FILES)
        } else {
            true
        }
    }
}
