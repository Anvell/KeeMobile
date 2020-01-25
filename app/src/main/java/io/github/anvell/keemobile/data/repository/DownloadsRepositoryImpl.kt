package io.github.anvell.keemobile.data.repository

import dagger.Reusable
import io.github.anvell.keemobile.common.constants.Directories
import io.github.anvell.keemobile.common.io.MediaStoreFile
import io.github.anvell.keemobile.domain.repository.DownloadsRepository
import java.io.IOException
import javax.inject.Inject

@Reusable
class DownloadsRepositoryImpl @Inject constructor(
    private val mediaStoreFile: MediaStoreFile
) : DownloadsRepository {

    override fun writeToDownloads(name: String, data: ByteArray) {
        mediaStoreFile.openOutputStream(name, Directories.DOWNLOADS)?.use { outputStream ->
            outputStream.write(data)
        } ?: throw IOException("Cannot write to file $name")
    }
}
