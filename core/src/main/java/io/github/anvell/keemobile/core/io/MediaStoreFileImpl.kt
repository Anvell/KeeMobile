package io.github.anvell.keemobile.core.io

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.anvell.keemobile.core.constants.AppConstants
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

@Reusable
class MediaStoreFileImpl @Inject constructor(@ApplicationContext private val context: Context) :
    MediaStoreFile {

    override fun openOutputStream(
        fileName: String,
        directory: String,
        overwrite: Boolean
    ): Pair<String, OutputStream>? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.RELATIVE_PATH, directory)
            }

            return context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            )?.let { uri ->
                context.contentResolver.openOutputStream(uri)?.let { stream ->
                    uri.toString() to stream
                }
            }

        } else {
            val dir = Environment.getExternalStoragePublicDirectory(directory).toString()
            var file = File(dir, fileName)
            val name = file.nameWithoutExtension

            if (!overwrite) {
                var num = 1
                while (file.exists()) {
                    file = File(dir, "$name ($num).${file.extension}")
                    num++
                }
            }

            return FileProvider.getUriForFile(
                context,
                AppConstants.FileProvider,
                file
            ).toString() to FileOutputStream(file)
        }
    }
}
