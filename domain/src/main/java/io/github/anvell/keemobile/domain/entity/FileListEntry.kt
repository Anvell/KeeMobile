package io.github.anvell.keemobile.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FileListEntry(
    @SerialName("fileSource")
    val fileSource: FileSource,
    @SerialName("encryptedSecrets")
    val encryptedSecrets: FileListEntrySecrets = FileListEntrySecrets.Unspecified
)
