package io.github.anvell.keemobile.domain.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AppSettings(
    val exploreViewMode: ViewMode = ViewMode.TREE
)
