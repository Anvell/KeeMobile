package io.github.anvell.keemobile.domain.entity

import java.util.*

data class KeyDeletedEntry(
    val uuid: UUID,
    val deletionTime: Calendar
)
