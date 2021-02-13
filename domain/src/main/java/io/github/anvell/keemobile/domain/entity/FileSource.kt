package io.github.anvell.keemobile.domain.entity

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import io.github.anvell.keemobile.domain.alias.VaultId
import kotlinx.android.parcel.Parcelize

sealed class FileSource(open val id: VaultId, open val name: String) : Parcelable {
    open val nameWithoutExtension: String
        get() = name.substringBeforeLast('.', name)

    @JsonClass(generateAdapter = true)
    @Parcelize
    class Storage(
        override val id: VaultId,
        override val name: String,
        val uri: String
    ) : FileSource(id, name)

}
