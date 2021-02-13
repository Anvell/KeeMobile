package io.github.anvell.keemobile.domain.entity

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

sealed class FileListEntrySecrets : Parcelable {
    @JsonClass(generateAdapter = true)
    @Parcelize
    class Unspecified : FileListEntrySecrets()

    @JsonClass(generateAdapter = true)
    @Parcelize
    class Never : FileListEntrySecrets()

    @JsonClass(generateAdapter = true)
    @Parcelize
    class Some(val fileSecrets: FileSecrets) : FileListEntrySecrets()
}
