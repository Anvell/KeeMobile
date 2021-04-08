package io.github.anvell.keemobile.domain.entity

data class OpenDatabase(
    val database: KeyDatabase,
    val source: FileSource,
    val secrets: FileSecrets
) {
    val id = VaultId(source.id)
}
