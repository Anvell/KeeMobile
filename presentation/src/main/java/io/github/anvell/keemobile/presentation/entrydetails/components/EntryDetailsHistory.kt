package io.github.anvell.keemobile.presentation.entrydetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.google.accompanist.insets.navigationBarsWithImePadding
import io.github.anvell.keemobile.core.extensions.formatAsDateTime
import io.github.anvell.keemobile.core.ui.components.Spacers
import io.github.anvell.keemobile.core.ui.locals.LocalAppNavigator
import io.github.anvell.keemobile.domain.entity.KeyEntry
import io.github.anvell.keemobile.domain.entity.VaultId
import io.github.anvell.keemobile.presentation.R
import io.github.anvell.keemobile.presentation.data.EntryType
import io.github.anvell.keemobile.presentation.entrydetails.data.EntryDetailsArgs
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
internal fun EntryDetailsHistory(
    activeDatabaseId: VaultId,
    entry: KeyEntry
) {
    val navigator = LocalAppNavigator.current
    val hapticFeedback = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current

    fun setClipboard(content: String) {
        clipboardManager.setText(AnnotatedString(content))
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .navigationBarsWithImePadding()
            .padding(vertical = dimensionResource(R.dimen.layout_vertical_margin))
    ) {
        entry.times?.let { EntryTimeDetails(it) }
        Spacers.M()

        if (entry.history.isNotEmpty()) {
            Text(
                text = stringResource(R.string.details_date_historic_title),
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(R.dimen.layout_horizontal_margin)
                )
            )
            Spacers.S()
            Divider()

            entry.history.forEach { item ->
                item.times?.lastModificationTime?.let { lastModification ->
                    LastModificationRow(
                        lastModificationTime = lastModification,
                        onClick = {
                            navigator.navigate(
                                R.id.action_historic_entry_details,
                                Json.encodeToString(
                                    EntryDetailsArgs(
                                        databaseId = activeDatabaseId,
                                        entryType = EntryType.Historic(
                                            id = entry.uuid.toString(),
                                            historicId = item.uuid.toString()
                                        )
                                    )
                                )
                            )
                        },
                        onLongClick = { setClipboard(lastModification.time.formatAsDateTime()) }
                    )
                    Divider()
                }
            }
        }
        Spacers.S()
    }
}
