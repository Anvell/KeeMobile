package io.github.anvell.keemobile.presentation.entrydetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import io.github.anvell.keemobile.core.authentication.OneTimePassword
import io.github.anvell.keemobile.core.extensions.formatAsDateTime
import io.github.anvell.keemobile.domain.entity.KeyEntry
import io.github.anvell.keemobile.presentation.R

private val OtpProperties = listOf("otp", "TOTP Seed", "TOTP Settings")

@Composable
internal fun EntryDetailsList(
    entry: KeyEntry
) {
    val oneTimePassword = remember(entry) { OneTimePassword.from(entry) }
    val hapticFeedback = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current

    fun setClipboard(content: String) {
        clipboardManager.setText(AnnotatedString(content))
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Column(Modifier.clip(MaterialTheme.shapes.medium)) {
        if (entry.username.isNotBlank()) {
            PropertyRow(
                title = stringResource(R.string.details_username),
                onLongClick = { setClipboard(entry.username) },
                content = { Text(text = entry.username) }
            )
            PropertyDivider()
        }

        if (entry.password.isNotBlank()) {
            MaskedPropertyRow(
                title = stringResource(R.string.details_password),
                onLongClick = { setClipboard(entry.password) },
                content = { Text(text = entry.password) }
            )
            PropertyDivider()
        }

        if (oneTimePassword != null) {
            OtpPropertyRow(
                otp = oneTimePassword,
                onLongClick = { setClipboard(it) }
            )
            PropertyDivider()
        }

        if (entry.notes.isNotBlank()) {
            PropertyRow(
                title = stringResource(R.string.details_notes),
                onLongClick = { setClipboard(entry.notes) },
                content = { Text(text = entry.notes) }
            )
            PropertyDivider()
        }

        if (entry.url.isNotBlank()) {
            PropertyRow(
                title = stringResource(R.string.details_website),
                onLongClick = { setClipboard(entry.url) }
            ) {
                Text(
                    text = entry.url,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            PropertyDivider()
        }

        entry.customProperties
            .filter { p -> !OtpProperties.any { it.equals(p.key, true) } }
            .forEach {
                if (it.isProtected) {
                    MaskedPropertyRow(
                        title = it.key,
                        onLongClick = { setClipboard(it.value) },
                        content = { Text(it.value) }
                    )
                } else {
                    PropertyRow(
                        title = it.key,
                        onLongClick = { setClipboard(it.value) },
                        content = { Text(it.value) }
                    )
                }
                PropertyDivider()
            }

        entry.times?.apply {
            if (expires) {
                expiryTime?.time?.formatAsDateTime()?.let {
                    PropertyRow(
                        title = stringResource(R.string.details_expires),
                        onLongClick = { setClipboard(it) },
                        content = { Text(it) }
                    )
                    PropertyDivider()
                }
            }
        }

        if (entry.tags.isNotEmpty()) {
            TagsPropertyRow(
                tags = entry.tags,
                onLongClick = { setClipboard(it) }
            )
        }
    }
}
