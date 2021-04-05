package io.github.anvell.keemobile.presentation.entrydetails.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import io.github.anvell.keemobile.core.extensions.formatAsDateTime
import io.github.anvell.keemobile.domain.entity.KeyDateTime
import io.github.anvell.keemobile.presentation.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun EntryTimeDetails(
    time: KeyDateTime
) {
    val hapticFeedback = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current

    fun setClipboard(content: String) {
        clipboardManager.setText(AnnotatedString(content))
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Column {
        time.creationTime?.time?.formatAsDateTime()?.let {
            PropertyRow(
                title = stringResource(R.string.details_date_created),
                onLongClick = { setClipboard(it) },
                background = Color.Transparent,
                content = { Text(it) }
            )
            Divider()
        }

        time.lastModificationTime?.time?.formatAsDateTime()?.let {
            PropertyRow(
                title = stringResource(R.string.details_date_updated),
                onLongClick = { setClipboard(it) },
                background = Color.Transparent,
                content = { Text(it) }
            )
        }
    }
}
