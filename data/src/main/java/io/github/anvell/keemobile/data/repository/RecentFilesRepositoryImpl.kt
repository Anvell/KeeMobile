package io.github.anvell.keemobile.data.repository

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.Reusable
import io.github.anvell.keemobile.core.constants.AppConstants
import io.github.anvell.keemobile.core.extensions.readAsString
import io.github.anvell.keemobile.core.io.InternalFile
import io.github.anvell.keemobile.core.io.StorageFile
import io.github.anvell.keemobile.domain.entity.FileListEntry
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.domain.repository.RecentFilesRepository
import java.io.IOException
import javax.inject.Inject

@Reusable
class RecentFilesRepositoryImpl @Inject constructor(
    private val internalFile: InternalFile,
    private val storageFile: StorageFile,
    private val moshi: Moshi
) : RecentFilesRepository {

    override fun readRecentFiles(): List<FileListEntry> {
        if (!internalFile.exists(AppConstants.FILE_RECENT_FILES)) {
            throw RuntimeException("${AppConstants.FILE_RECENT_FILES} does not exist.")
        }

        internalFile.openInputStream(AppConstants.FILE_RECENT_FILES)?.use { stream ->
            val type = Types.newParameterizedType(List::class.java, FileListEntry::class.java)
            return moshi.adapter<List<FileListEntry>>(type)
                .fromJson(stream.readAsString())
                ?.filter {
                    when (val source = it.fileSource) {
                        is FileSource.Storage -> {
                            storageFile.checkUriPermission(source.uri) && storageFile.exists(source.uri)
                        }
                        else -> true
                    }
                } ?: throw IOException("Failed to parse ${AppConstants.FILE_RECENT_FILES}")
        }

        throw IOException("Cannot open ${AppConstants.FILE_RECENT_FILES}")
    }

    override fun writeRecentFiles(recentFiles: List<FileListEntry>) {
        internalFile.openOutputStream(AppConstants.FILE_RECENT_FILES)?.use { stream ->
            val type = Types.newParameterizedType(List::class.java, FileListEntry::class.java)
            val data = moshi.adapter<List<FileListEntry>>(type).toJson(recentFiles)
            stream.write(data.toByteArray())
            return
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
