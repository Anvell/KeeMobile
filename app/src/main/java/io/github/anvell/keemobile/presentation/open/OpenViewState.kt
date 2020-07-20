package io.github.anvell.keemobile.presentation.open

import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.Async
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.domain.entity.Uninitialized

data class OpenViewState(
    val initialSetup: Boolean = true,
    val recentFiles: Async<List<FileSource>> = Uninitialized,
    val recentFilesStash: List<FileSource>? = null,
    val selectedFile: FileSource? = null,
    val openFile: Async<VaultId> = Uninitialized
)
