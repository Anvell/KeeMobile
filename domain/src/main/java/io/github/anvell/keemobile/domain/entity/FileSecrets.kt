package io.github.anvell.keemobile.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class FileSecrets

@Serializable
@SerialName("KeyOnly")
data class KeyOnly(
    @SerialName("masterKey")
    val masterKey: Secret
) : FileSecrets()

@Serializable
@SerialName("KeyFileOnly")
data class KeyFileOnly(
    @SerialName("keyFile")
    val keyFile: FileSource
) : FileSecrets()

@Serializable
@SerialName("KeyWithKeyFile")
data class KeyWithKeyFile(
    @SerialName("masterKey")
    val masterKey: Secret,
    @SerialName("keyFile")
    val keyFile: FileSource
) : FileSecrets()
