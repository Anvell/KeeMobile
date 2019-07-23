package io.github.anvell.keemobile.domain.entity

import java.util.*

data class KeyDateTime(
    val creationTime: Calendar?,
    val lastAccessTime: Calendar?,
    val lastModificationTime: Calendar?,
    val locationChanged: Calendar?,
    val expiryTime: Calendar?,
    val usageCount: Int = 0,
    val expires: Boolean = false
)
