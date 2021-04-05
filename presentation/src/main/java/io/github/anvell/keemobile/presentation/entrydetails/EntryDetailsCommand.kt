package io.github.anvell.keemobile.presentation.entrydetails

import io.github.anvell.keemobile.domain.entity.KeyAttachment

sealed class EntryDetailsCommand {
    class SaveAttachment(val attachment: KeyAttachment) : EntryDetailsCommand()
}
