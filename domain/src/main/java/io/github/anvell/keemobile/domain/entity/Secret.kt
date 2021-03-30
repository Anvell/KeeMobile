package io.github.anvell.keemobile.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Secret {
    @Serializable
    @SerialName("Encrypted")
    class Encrypted(
        @SerialName("iv")
        val iv: ByteArray,
        @SerialName("content")
        val content: ByteArray
    ) : Secret()

    @Serializable
    @SerialName("Unencrypted")
    data class Unencrypted(
        @SerialName("content")
        val content: String
    ) : Secret()
}
