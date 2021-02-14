package io.github.anvell.keemobile.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class FileListEntry(
    val fileSource: FileSource,
    val encryptedSecrets: FileListEntrySecrets = FileListEntrySecrets.Unspecified
) : Parcelable
