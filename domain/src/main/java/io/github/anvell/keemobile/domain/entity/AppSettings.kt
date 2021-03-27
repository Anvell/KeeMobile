package io.github.anvell.keemobile.domain.entity

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
    val exploreViewMode: ViewMode = ViewMode.TREE
)
