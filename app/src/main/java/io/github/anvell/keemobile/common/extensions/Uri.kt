package io.github.anvell.keemobile.common.extensions

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

fun Uri.getName(context: Context) = DocumentFile.fromSingleUri(context, this)?.name
