package io.github.anvell.keemobile.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class FileListEntrySecrets {
    @Serializable
    @SerialName("Unspecified")
    object Unspecified : FileListEntrySecrets()

    @Serializable
    @SerialName("Never")
    object Never : FileListEntrySecrets()

    @Serializable
    @SerialName("Some")
    class Some(
        @SerialName("fileSecrets")
        val fileSecrets: FileSecrets
    ) : FileListEntrySecrets()
}
