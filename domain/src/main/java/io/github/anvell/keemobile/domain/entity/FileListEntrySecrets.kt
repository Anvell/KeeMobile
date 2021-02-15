package io.github.anvell.keemobile.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
sealed class FileListEntrySecrets : Parcelable {
    @Parcelize
    @Serializable
    object Unspecified : FileListEntrySecrets()

    @Parcelize
    @Serializable
    object Never : FileListEntrySecrets()

    @Parcelize
    @Serializable
    class Some(val fileSecrets: FileSecrets) : FileListEntrySecrets()
}
