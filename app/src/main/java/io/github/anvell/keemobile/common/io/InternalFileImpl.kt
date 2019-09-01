package io.github.anvell.keemobile.common.io

import android.content.Context
import dagger.Reusable
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

@Reusable
class InternalFileImpl @Inject constructor(private val context: Context) :
    InternalFile {

    override fun openInputStream(fileName: String): InputStream? {
        return context.openFileInput(fileName)
    }

    override fun openOutputStream(fileName: String): OutputStream? {
        return context.openFileOutput(fileName, Context.MODE_PRIVATE)
    }
}
