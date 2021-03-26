package io.github.anvell.keemobile.domain.repository

import io.github.anvell.keemobile.domain.datatypes.Either
import io.github.anvell.keemobile.domain.entity.FileListEntry

interface RecentFilesRepository {

    fun readRecentFiles(): Either<Exception, List<FileListEntry>>

    fun writeRecentFiles(recentFiles: List<FileListEntry>): Either<Exception, List<FileListEntry>>

    fun clearRecentFiles(): Either<Exception, Unit>
}
