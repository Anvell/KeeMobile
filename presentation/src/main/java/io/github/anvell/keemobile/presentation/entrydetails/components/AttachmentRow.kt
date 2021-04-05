package io.github.anvell.keemobile.presentation.entrydetails.components

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.format.Formatter
import androidx.activity.compose.registerForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.anvell.keemobile.core.extensions.getMimeTypeFromFileName
import io.github.anvell.keemobile.core.ui.components.Spacers
import io.github.anvell.keemobile.domain.entity.KeyAttachment
import io.github.anvell.keemobile.presentation.R
import io.github.anvell.keemobile.presentation.entrydetails.data.AttachmentState

@Composable
internal fun AttachmentRow(
    attachment: KeyAttachment,
    length: Long,
    state: AttachmentState,
    onSaveAttachment: (KeyAttachment) -> Unit
) {
    val context = LocalContext.current
    val mimeType = attachment.key.getMimeTypeFromFileName()
    val requestPermissionContract = remember { ActivityResultContracts.RequestPermission() }
    val requestPermissionLauncher = registerForActivityResult(requestPermissionContract) {
        if (it == true) onSaveAttachment(attachment)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable {
                when (state) {
                    is AttachmentState.Download -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            onSaveAttachment(attachment)
                        } else {
                            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    }
                    is AttachmentState.Open -> {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(Uri.parse(state.uri), mimeType)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        }
                        with(context) {
                            startActivity(
                                Intent.createChooser(
                                    intent,
                                    getString(R.string.open_dialogs_open_with)
                                )
                            )
                        }
                    }
                    else -> Unit
                }
            }
            .padding(
                horizontal = dimensionResource(R.dimen.layout_horizontal_margin),
                vertical = dimensionResource(R.dimen.content_margin)
            )
    ) {
        Column(Modifier.weight(1f)) {
            Text(attachment.key)
            Spacers.Xxs()

            Text(
                text = Formatter.formatShortFileSize(context, length),
                style = MaterialTheme.typography.caption
            )
        }

        when (state) {
            is AttachmentState.Download -> {
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
            is AttachmentState.Processing -> {
                CircularProgressIndicator(
                    strokeWidth = 2.5.dp,
                    modifier = Modifier.size(24.dp)
                )
            }
            is AttachmentState.Open -> {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_simple_forward),
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
