package io.github.anvell.keemobile.common.io

import android.content.Context
import android.net.Uri
import dagger.Reusable
import java.io.InputStream
import javax.inject.Inject

@Reusable
class StorageFileImpl @Inject constructor(private val context: Context) :
    StorageFile {

    override fun readFromUri(sourceUri: String): InputStream? {
        val uri = Uri.parse(sourceUri)
        return context.contentResolver.openInputStream(uri)
    }
}
