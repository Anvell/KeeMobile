package io.github.anvell.keemobile.presentation.data

import kotlinx.serialization.Serializable

@Serializable
sealed class EntryType {
    @Serializable
    data class Actual(val id: String) : EntryType()

    @Serializable
    data class Historic(val id: String, val historicId: String) : EntryType()
}
