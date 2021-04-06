package io.github.anvell.keemobile.presentation.open

import io.github.anvell.keemobile.domain.entity.FileListEntry
import io.github.anvell.keemobile.domain.entity.FileListEntrySecrets
import io.github.anvell.keemobile.domain.entity.FileSecrets
import io.github.anvell.keemobile.domain.entity.FileSource

sealed class OpenCommand {
    object ClearRecentFiles : OpenCommand()

    class RemoveRecentFile(
        val entry: FileListEntry,
    ) : OpenCommand()

    class AddFileSource(
        val source: FileSource
    ) : OpenCommand()

    class CreateFile(
        val source: FileSource,
        val secrets: FileSecrets
    ) : OpenCommand()

    class SelectFile(
        val source: FileListEntry
    ) : OpenCommand()

    class OpenFile(
        val entry: FileListEntry,
        val secrets: FileSecrets
    ) : OpenCommand()
}
