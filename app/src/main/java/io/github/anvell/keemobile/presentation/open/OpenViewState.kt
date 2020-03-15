package io.github.anvell.keemobile.presentation.open

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.FileSource
import java.util.*

data class OpenViewState(
    val initialSetup: Boolean = true,
    val recentFiles: Async<List<FileSource>> = Uninitialized,
    val selectedFile: FileSource? = null,
    val openFile: Async<VaultId> = Uninitialized
) : MvRxState
