package io.github.anvell.keemobile.presentation.open.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import io.github.anvell.keemobile.core.ui.components.Spacers
import io.github.anvell.keemobile.presentation.R

@Composable
internal fun LandingBlock(
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(R.drawable.crystal),
            contentDescription = null
        )
        Spacers.L()
        Text(
            text = stringResource(R.string.open_landing_title),
            style = MaterialTheme.typography.h4.copy(
                color = MaterialTheme.colors.onBackground
            )
        )
        Spacers.M()
        Text(
            text = stringResource(R.string.open_landing_body_text),
            textAlign = TextAlign.Center
        )
    }
}
