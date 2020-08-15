package io.github.anvell.keemobile.core.io

import java.io.InputStream
import java.io.OutputStream

interface InternalFile {

    fun openInputStream(fileName: String): InputStream?

    fun openOutputStream(fileName: String): OutputStream?

    fun exists(fileName: String): Boolean

    fun remove(fileName: String): Boolean
}
