package io.github.anvell.keemobile.domain.entity

data class FileSecrets(
    val masterKey: String? = null,
    val keyFile: FileSource? = null
)
