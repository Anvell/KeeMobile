package io.github.anvell.keemobile.presentation.explore.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import io.github.anvell.keemobile.presentation.R

@Composable
internal fun ExploreEmptyList(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(
                horizontal = dimensionResource(R.dimen.content_margin_medium),
                vertical = dimensionResource(R.dimen.content_margin)
            )
    ) {
        Text(
            text = AnnotatedString(title).toUpperCase(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.button
        )
    }
}
