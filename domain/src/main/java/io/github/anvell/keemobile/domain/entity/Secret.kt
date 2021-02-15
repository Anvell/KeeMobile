package io.github.anvell.keemobile.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
sealed class Secret : Parcelable {
    @Parcelize
    @Serializable
    class Encrypted(
        val iv: ByteArray,
        val content: ByteArray
    ) : Secret()

    @Parcelize
    @Serializable
    data class Unencrypted(val content: String) : Secret()
}
