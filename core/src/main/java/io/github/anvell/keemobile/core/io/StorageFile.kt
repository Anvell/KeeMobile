package io.github.anvell.keemobile.core.io

import java.io.InputStream
import java.io.OutputStream

interface StorageFile {

    fun openInputStream(sourceUri: String): InputStream?

    fun openOutputStream(targetUri: String): OutputStream?

    fun checkUriPermission(targetUri: String): Boolean

    fun exists(targetUri: String): Boolean
}
