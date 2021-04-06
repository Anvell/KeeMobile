package io.github.anvell.keemobile.presentation.explore.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.google.accompanist.insets.imePadding
import com.google.accompanist.insets.navigationBarsWithImePadding
import io.github.anvell.keemobile.core.ui.mappers.FilterColorMapper
import io.github.anvell.keemobile.core.ui.mappers.IconMapper
import io.github.anvell.keemobile.core.ui.theme.AppTheme
import io.github.anvell.keemobile.domain.entity.KeyEntry
import io.github.anvell.keemobile.domain.entity.KeyGroup

@Composable
internal fun GroupAsList(
    group: KeyGroup,
    onGroupClicked: (KeyGroup) -> Unit,
    onEntryClicked: (KeyEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier) {
        items(group.groups, key = { it.uuid }) { item ->
            ExploreItem(
                title = item.name,
                description = item.notes,
                icon = {
                    Icon(
                        painter = painterResource(IconMapper.map(item.iconId)),
                        contentDescription = null,
                        tint = MaterialTheme.colors.onSurface
                    )
                },
                onClick = { onGroupClicked(item) }
            )
        }

        items(group.entries, key = { it.uuid }) { item ->
            ExploreItem(
                title = item.name,
                description = item.username,
                icon = {
                    Icon(
                        painter = painterResource(IconMapper.map(item.iconId)),
                        contentDescription = null,
                        tint = item.backgroundColor?.let {
                            FilterColorMapper.map(it, AppTheme.colors.filterColors)
                        } ?: MaterialTheme.colors.onSurface
                    )
                },
                onClick = { onEntryClicked(item) }
            )
        }

        item { Spacer(Modifier.navigationBarsWithImePadding()) }
    }
}
