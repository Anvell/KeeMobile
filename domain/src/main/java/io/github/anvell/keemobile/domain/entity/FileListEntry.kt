package io.github.anvell.keemobile.domain.entity

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class FileListEntry(
    val fileSource: FileSource,
    val encryptedSecrets: FileListEntrySecrets = FileListEntrySecrets.Unspecified()
) : Parcelable
