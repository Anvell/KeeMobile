package io.github.anvell.keemobile.common.io

import java.io.InputStream
import java.io.OutputStream

interface StorageFile {

    fun openInputStream(sourceUri: String): InputStream?

    fun openOutputStream(targetUri: String): OutputStream?
}
