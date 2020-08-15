package io.github.anvell.keemobile.data.repository

import dagger.Reusable
import io.github.anvell.keemobile.core.constants.Directories
import io.github.anvell.keemobile.core.io.MediaStoreFile
import io.github.anvell.keemobile.domain.repository.DownloadsRepository
import java.io.IOException
import javax.inject.Inject

@Reusable
class DownloadsRepositoryImpl @Inject constructor(
    private val mediaStoreFile: MediaStoreFile
) : DownloadsRepository {

    override fun writeToDownloads(name: String, data: ByteArray): String {
        val (uri, stream) = mediaStoreFile.openOutputStream(name, Directories.DOWNLOADS)
            ?: throw IOException("Cannot create file $name")

        stream.use { outputStream ->
            outputStream.write(data)
            return uri
        }
    }
}
