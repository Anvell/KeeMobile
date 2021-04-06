package io.github.anvell.keemobile.presentation.entrydetails.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import io.github.anvell.keemobile.core.extensions.formatAsDateTime
import io.github.anvell.keemobile.core.ui.components.Spacers
import io.github.anvell.keemobile.presentation.R
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LastModificationRow(
    lastModificationTime: Calendar,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .combinedClickable(
                onLongClick = onLongClick,
                onClick = onClick
            )
            .padding(horizontal = dimensionResource(R.dimen.layout_horizontal_margin))
    ) {
        Text(
            text = lastModificationTime.time.formatAsDateTime(),
            modifier = Modifier
                .padding(vertical = dimensionResource(R.dimen.content_margin))
                .weight(1f)
        )
        Spacers.S()

        Icon(
            painter = painterResource(R.drawable.ic_clock),
            contentDescription = null,
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
    }
}
