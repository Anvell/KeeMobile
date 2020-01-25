package io.github.anvell.keemobile.common.io

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import dagger.Reusable
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

@Reusable
class MediaStoreFileImpl @Inject constructor(private val context: Context) :
    MediaStoreFile {

    override fun openOutputStream(fileName: String, directory: String, overwrite: Boolean): OutputStream? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.RELATIVE_PATH, directory)
            }

            context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)?.let {
                return context.contentResolver.openOutputStream(it)
            }

            return null

        } else {
            val dir = Environment.getExternalStoragePublicDirectory(directory).toString()
            var file = File(dir, fileName)
            val name = file.nameWithoutExtension

            return if (overwrite) {
                FileOutputStream(file)
            } else {
                var num = 1
                while (file.exists()) {
                    file = File(dir, "$name ($num).${file.extension}")
                    num++
                }

                FileOutputStream(file)
            }
        }
    }
}
