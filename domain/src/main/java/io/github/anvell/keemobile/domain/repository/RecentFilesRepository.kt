package io.github.anvell.keemobile.domain.repository

import io.github.anvell.keemobile.domain.entity.FileSource

interface RecentFilesRepository {

    fun readRecentFiles(): List<FileSource>

    fun writeRecentFiles(recentFiles: List<FileSource>)

    fun clearRecentFiles(): Boolean
}
