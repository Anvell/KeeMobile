package io.github.anvell.keemobile.presentation.entrydetails.data

sealed class AttachmentState {
    object Download : AttachmentState()
    object Processing : AttachmentState()
    class Open(val uri: String) : AttachmentState()
}
