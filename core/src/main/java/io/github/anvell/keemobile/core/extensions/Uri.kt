package io.github.anvell.keemobile.core.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

fun Uri.getName(context: Context) = DocumentFile.fromSingleUri(context, this)?.name

fun Uri.fileExists(context: Context) = DocumentFile.fromSingleUri(context, this)?.exists() ?: false

fun Uri.persistReadWritePermissions(context: Context) {
    context.contentResolver.takePersistableUriPermission(
        this, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    )
}
