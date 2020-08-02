package io.github.anvell.keemobile.core.extensions

import java.io.BufferedReader
import java.io.InputStream

fun InputStream.readAsString(): String {
    val reader = BufferedReader(reader())
    val content = StringBuilder()

    reader.use {
        var line = reader.readLine()
        while (line != null) {
            content.append(line)
            line = reader.readLine()
        }
    }
    return content.toString()
}
