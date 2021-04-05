package io.github.anvell.keemobile.presentation.entrydetails.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.anvell.keemobile.core.authentication.OneTimePassword
import io.github.anvell.keemobile.core.ui.components.Spacers
import io.github.anvell.keemobile.presentation.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.Instant
import kotlin.math.ceil

private const val ProgressUpdateDelay = 1000L

private val ProgressIndicatorWidth = 3.dp
private val ProgressIndicatorSize = 24.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun OtpPropertyRow(
    otp: OneTimePassword,
    modifier: Modifier = Modifier,
    onLongClick: ((String) -> Unit)? = null,
) {
    var progressValue by remember { mutableStateOf(0f) }
    var otpValue by rememberSaveable { mutableStateOf("") }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onLongClick = { onLongClick?.invoke(otpValue) },
                onClick = {}
            )
            .background(MaterialTheme.colors.surface)
            .padding(
                horizontal = dimensionResource(R.dimen.content_margin),
                vertical = dimensionResource(R.dimen.content_margin)
            )
            .fillMaxWidth()
            .then(modifier)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.details_otp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.caption
            )
            Spacers.Xxs()

            Text(text = otpValue)
        }
        Spacers.S()

        Box {
            CircularProgressIndicator(
                progress = 1f,
                strokeWidth = ProgressIndicatorWidth,
                color = MaterialTheme.colors.background,
                modifier = Modifier.size(ProgressIndicatorSize)
            )
            CircularProgressIndicator(
                progress = progressValue,
                strokeWidth = ProgressIndicatorWidth,
                modifier = Modifier.size(ProgressIndicatorSize)
            )
        }
    }

    LaunchedEffect(otp) {
        var currentStep = 0.0

        do {
            val step = Instant.now().epochSecond.toDouble() / otp.period
            val rounded = ceil(step)
            progressValue = 1 - (rounded - step).toFloat()

            if (rounded > currentStep) {
                currentStep = rounded
                otpValue = otp.calculate()
            }
            delay(ProgressUpdateDelay)
        } while (isActive)
    }
}
