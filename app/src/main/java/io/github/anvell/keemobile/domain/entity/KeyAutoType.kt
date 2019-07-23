package io.github.anvell.keemobile.domain.entity

data class KeyAutoType(
    val isEnabled: Boolean,
    val dataTransferObfuscation: Int,
    val defaultSequence: String?,
    val associations: MutableList<KeyAutoTypeAssociation> = mutableListOf()
)
