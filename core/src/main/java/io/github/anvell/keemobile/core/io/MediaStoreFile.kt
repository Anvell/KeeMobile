package io.github.anvell.keemobile.core.io

import java.io.OutputStream

interface MediaStoreFile {

    fun openOutputStream(fileName: String, directory: String, overwrite: Boolean = false): Pair<String, OutputStream>?
}
