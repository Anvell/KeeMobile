package io.github.anvell.keemobile.common.io

import java.io.OutputStream

interface MediaStoreFile {

    fun openOutputStream(fileName: String, directory: String, overwrite: Boolean = false): Pair<String, OutputStream>?
}
