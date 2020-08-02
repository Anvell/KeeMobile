package io.github.anvell.keemobile.core.io

interface ClipboardProvider {

    fun putText(label: String, content: String): Boolean
}
