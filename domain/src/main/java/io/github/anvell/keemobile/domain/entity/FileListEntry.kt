package io.github.anvell.keemobile.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FileListEntry(
    @SerialName("vault")
    val vault: FileSource,
    @SerialName("keyFile")
    val keyFile: FileSource? = null,
    @SerialName("encryptedSecrets")
    val encryptedSecrets: FileListEntrySecrets = FileListEntrySecrets.Unspecified
)
