package io.github.anvell.keemobile.data.repository

import com.gitlab.mvysny.konsumexml.konsumeXml
import dagger.Reusable
import io.github.anvell.keemobile.common.constants.AppConstants
import io.github.anvell.keemobile.common.extensions.readAsString
import io.github.anvell.keemobile.common.extensions.toXmlTag
import io.github.anvell.keemobile.common.io.InternalFile
import io.github.anvell.keemobile.common.io.StorageFile
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.domain.repository.RecentFilesRepository
import java.io.IOException
import javax.inject.Inject

@Reusable
class RecentFilesRepositoryImpl @Inject constructor(
    private val internalFile: InternalFile,
    private val storageFile: StorageFile
) : RecentFilesRepository {

    companion object {
        private const val RECENT_FILES_TAG = "recent-files"
    }

    override fun readRecentFiles(): List<FileSource> {
        internalFile.openInputStream(AppConstants.FILE_RECENT_FILES)?.use { stream ->
            return stream
                .readAsString()
                .konsumeXml()
                .child(RECENT_FILES_TAG) {
                    checkCurrent(RECENT_FILES_TAG)
                    children(FileSource.Storage.TAG) {
                        checkCurrent(FileSource.Storage.TAG)

                        FileSource.Storage(
                            childText(FileSource.Storage.ID),
                            childText(FileSource.Storage.NAME),
                            childText(FileSource.Storage.URI)
                        )
                    }
                }.filter {
                    storageFile.checkUriPermission(it.uri) && storageFile.exists(it.uri)
                }
        }

        throw IOException("Cannot open ${AppConstants.FILE_RECENT_FILES}")
    }

    override fun writeRecentFiles(recentFiles: List<FileSource>) {

        val contents = recentFiles
            .map(FileSource::toXml)
            .reduce { acc, element -> acc + element }
            .toXmlTag(RECENT_FILES_TAG)

        internalFile.openOutputStream(AppConstants.FILE_RECENT_FILES)?.use { stream ->
            stream.write(contents.toByteArray())
            return
        }

        throw IOException("Cannot write ${AppConstants.FILE_RECENT_FILES}")
    }
}
