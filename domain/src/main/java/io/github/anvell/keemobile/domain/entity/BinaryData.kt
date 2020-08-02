package io.github.anvell.keemobile.domain.entity

data class BinaryData(
    val id: Int,
    val isCompressed: Boolean,
    val data: ByteArray
)
