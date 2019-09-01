package io.github.anvell.keemobile.common.io

import java.io.InputStream
import java.io.OutputStream

interface InternalFile {

    fun openInputStream(fileName: String): InputStream?

    fun openOutputStream(fileName: String): OutputStream?
}
