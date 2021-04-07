package io.github.anvell.keemobile.domain.repository

import io.github.anvell.either.Either
import io.github.anvell.keemobile.domain.entity.FileListEntry
import kotlinx.coroutines.flow.SharedFlow

interface RecentFilesRepository {
    val recentFilesAsFlow: SharedFlow<List<FileListEntry>>

    suspend fun readRecentFiles(): Either<Exception, List<FileListEntry>>

    suspend fun writeRecentFiles(items: List<FileListEntry>): Either<Exception, List<FileListEntry>>

    suspend fun clearRecentFiles(): Either<Exception, Unit>
}
