package io.github.anvell.keemobile.domain.entity

import com.squareup.moshi.JsonClass

enum class ViewMode {
    TREE,
    LIST
}

@JsonClass(generateAdapter = true)
data class AppSettings(
    val exploreViewMode: ViewMode = ViewMode.TREE
)
