package io.github.anvell.keemobile.presentation.open

import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.datatypes.Async
import io.github.anvell.keemobile.domain.datatypes.Uninitialized
import io.github.anvell.keemobile.domain.entity.FileListEntry

data class OpenViewState(
    val initialSetup: Boolean = true,
    val recentFiles: Async<List<FileListEntry>> = Uninitialized,
    val recentFilesStash: List<FileListEntry>? = null,
    val selectedFile: FileListEntry? = null,
    val openFile: Async<VaultId> = Uninitialized
)
