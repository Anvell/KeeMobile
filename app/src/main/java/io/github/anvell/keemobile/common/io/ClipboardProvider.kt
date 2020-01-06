package io.github.anvell.keemobile.common.io

interface ClipboardProvider {

    fun putText(label: String, content: String): Boolean
}
