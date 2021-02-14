package io.github.anvell.keemobile.domain.entity

import android.os.Parcelable
import io.github.anvell.keemobile.domain.alias.VaultId
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
sealed class FileSource : Parcelable {
    abstract val id: VaultId
    abstract val name: String

    open val nameWithoutExtension: String
        get() = name.substringBeforeLast('.', name)

    @Parcelize
    @Serializable
    class Storage(
        override val id: VaultId,
        override val name: String,
        val uri: String
    ) : FileSource()
}
