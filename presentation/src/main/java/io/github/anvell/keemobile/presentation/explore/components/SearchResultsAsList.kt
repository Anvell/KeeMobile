package io.github.anvell.keemobile.presentation.explore.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.github.anvell.keemobile.core.ui.mappers.ComposeFilterColorMapper
import io.github.anvell.keemobile.core.ui.mappers.IconMapper
import io.github.anvell.keemobile.core.ui.theme.AppTheme
import io.github.anvell.keemobile.domain.entity.KeyEntry
import io.github.anvell.keemobile.domain.entity.SearchResult
import io.github.anvell.keemobile.presentation.R
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun SearchResultsAsList(
    results: List<SearchResult>,
    onEntryClicked: (KeyEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sortedResults = remember(results) {
        results.sortedBy { it.group.name.toLowerCase(Locale.getDefault()) }
    }
    LazyColumn(modifier) {
        if (results.isEmpty()) {
            item {
                ExploreEmptyList(stringResource(R.string.explore_search_no_results))
            }
        } else {
            sortedResults.forEach { searchResult ->
                val entries = searchResult.entries.sortedBy { it.name }

                stickyHeader(key = searchResult.group.uuid) {
                    ExploreHeader(searchResult.group.name)
                }

                items(entries, key = { it.uuid }) { item ->
                    ExploreItem(
                        title = item.name,
                        description = item.username,
                        icon = {
                            Icon(
                                painter = painterResource(IconMapper.map(item.iconId)),
                                contentDescription = null,
                                tint = item.backgroundColor?.let {
                                    ComposeFilterColorMapper.map(it, AppTheme.colors.filterColors)
                                } ?: MaterialTheme.colors.onSurface
                            )
                        },
                        onClick = { onEntryClicked(item) }
                    )
                }
            }
        }
    }
}
