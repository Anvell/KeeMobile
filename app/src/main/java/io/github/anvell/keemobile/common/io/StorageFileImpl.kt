package io.github.anvell.keemobile.common.io

import android.content.Context
import android.net.Uri
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.anvell.keemobile.common.extensions.fileExists
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

@Reusable
class StorageFileImpl @Inject constructor(@ApplicationContext private val context: Context) :
    StorageFile {

    override fun openInputStream(sourceUri: String): InputStream? {
        val uri = Uri.parse(sourceUri)
        return context.contentResolver.openInputStream(uri)
    }

    override fun openOutputStream(targetUri: String): OutputStream? {
        val uri = Uri.parse(targetUri)
        return context.contentResolver.openOutputStream(uri)
    }

    override fun checkUriPermission(targetUri: String): Boolean {
        val uri = Uri.parse(targetUri)
        return context.contentResolver.persistedUriPermissions.any { it.uri == uri }
    }

    override fun exists(targetUri: String) = Uri.parse(targetUri).fileExists(context)
}
