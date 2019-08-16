package io.github.anvell.keemobile.common.io

import java.io.InputStream

interface StorageFile {

    fun readFromUri(sourceUri: String): InputStream?
}
