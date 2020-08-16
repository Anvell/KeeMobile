package io.github.anvell.keemobile.domain.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FileListEntry(
    val fileSource: FileSource,
    val encryptedSecrets: FileSecrets? = null
)
