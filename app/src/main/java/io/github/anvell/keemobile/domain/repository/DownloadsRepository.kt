package io.github.anvell.keemobile.domain.repository

interface DownloadsRepository {

    fun writeToDownloads(name: String, data: ByteArray): String
}
