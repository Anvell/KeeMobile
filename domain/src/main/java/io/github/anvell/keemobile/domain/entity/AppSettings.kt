package io.github.anvell.keemobile.domain.entity

import kotlinx.serialization.Serializable

enum class ViewMode {
    TREE,
    LIST
}

@Serializable
data class AppSettings(
    val exploreViewMode: ViewMode = ViewMode.TREE
)
