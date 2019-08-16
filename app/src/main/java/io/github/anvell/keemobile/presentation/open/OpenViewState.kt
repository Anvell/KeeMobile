package io.github.anvell.keemobile.presentation.open

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import io.github.anvell.keemobile.domain.entity.FileSource
import java.util.*

data class OpenViewState(
    val recentFiles: List<FileSource> = listOf(),
    val selectedFile: FileSource? = null,
    val opened: Async<UUID> = Uninitialized
) : MvRxState
