package io.github.anvell.keemobile.presentation.entrydetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import io.github.anvell.keemobile.domain.entity.BinaryData
import io.github.anvell.keemobile.domain.entity.KeyAttachment
import io.github.anvell.keemobile.domain.entity.KeyEntry
import io.github.anvell.keemobile.presentation.R
import io.github.anvell.keemobile.presentation.entrydetails.data.AttachmentState

@Composable
internal fun EntryDetailsAttachments(
    entry: KeyEntry,
    binaries: List<BinaryData>,
    saveAttachmentQueue: Set<Int>,
    savedAttachments: Map<Int, String>,
    onSaveAttachment: (KeyAttachment) -> Unit
) {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = dimensionResource(R.dimen.layout_vertical_margin))
    ) {
        for (item in entry.attachments) {
            val fileLength = binaries.find { it.id == item.ref }?.data?.size?.toLong() ?: 0L
            val isProcessing = saveAttachmentQueue.contains(item.ref)
            val savedFileUri = savedAttachments[item.ref]

            AttachmentRow(
                attachment = item,
                length = fileLength,
                state = when {
                    isProcessing -> AttachmentState.Processing
                    savedFileUri != null -> AttachmentState.Open(savedFileUri)
                    else -> AttachmentState.Download
                },
                onSaveAttachment = onSaveAttachment
            )
            Divider()
        }
    }
}
