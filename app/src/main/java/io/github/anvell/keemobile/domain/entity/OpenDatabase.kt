package io.github.anvell.keemobile.domain.entity

import java.util.*

data class OpenDatabase(
    val id: UUID,
    val database: KeyDatabase,
    val source: FileSource,
    val secrets: FileSecrets
)
