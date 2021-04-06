package io.github.anvell.keemobile.presentation.entrydetails.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.anvell.keemobile.core.ui.components.Spacers
import io.github.anvell.keemobile.presentation.R

@Composable
fun EntryDetailsButton(
    text: String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .weight(1f)
        )
        Spacers.S()

        Icon(
            painter = painterResource(R.drawable.ic_arrow_simple_forward),
            contentDescription = null,
            tint = MaterialTheme.colors.onSurface
        )
    }
}
