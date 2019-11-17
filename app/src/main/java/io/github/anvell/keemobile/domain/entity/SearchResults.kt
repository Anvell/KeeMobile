package io.github.anvell.keemobile.domain.entity

data class SearchResults(
    val filter: String,
    val filteredEntries: List<SearchResult>
)
