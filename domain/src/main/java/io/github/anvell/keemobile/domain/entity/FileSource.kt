package io.github.anvell.keemobile.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class FileSource {
    abstract val id: String
    abstract val name: String

    open val nameWithoutExtension: String
        get() = name.substringBeforeLast('.', name)

    @Serializable
    @SerialName("Storage")
    class Storage(
        @SerialName("id")
        override val id: String,
        @SerialName("name")
        override val name: String,
        @SerialName("uri")
        val uri: String
    ) : FileSource()
}
