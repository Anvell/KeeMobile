package io.github.anvell.keemobile.domain.entity

import com.squareup.moshi.JsonClass

sealed class FileSecrets

@JsonClass(generateAdapter = true)
data class KeyOnly(val masterKey: String) : FileSecrets()

@JsonClass(generateAdapter = true)
data class KeyFileOnly(val keyFile: FileSource) : FileSecrets()

@JsonClass(generateAdapter = true)
data class KeyWithKeyFile(val masterKey: String, val keyFile: FileSource) : FileSecrets()
