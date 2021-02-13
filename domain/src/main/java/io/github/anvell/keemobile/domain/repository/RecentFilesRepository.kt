package io.github.anvell.keemobile.domain.repository

import io.github.anvell.keemobile.domain.entity.FileListEntry

interface RecentFilesRepository {

    fun readRecentFiles(): List<FileListEntry>

    fun writeRecentFiles(recentFiles: List<FileListEntry>): List<FileListEntry>

    fun clearRecentFiles(): Boolean
}
