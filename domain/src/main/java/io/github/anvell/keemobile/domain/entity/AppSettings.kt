package io.github.anvell.keemobile.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ViewMode {
    TREE,
    LIST;

    operator fun not() = when (this) {
        TREE -> LIST
        LIST -> TREE
    }
}

@Serializable
data class AppSettings(
    @SerialName("exploreViewMode")
    val exploreViewMode: ViewMode = ViewMode.TREE
)
