package io.github.anvell.keemobile.presentation.open

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.anvell.keemobile.core.constants.AppConstants
import io.github.anvell.keemobile.core.ui.extensions.append
import io.github.anvell.keemobile.core.ui.mvi.MviRxViewModel
import io.github.anvell.keemobile.domain.datatypes.Success
import io.github.anvell.keemobile.domain.datatypes.Uninitialized
import io.github.anvell.keemobile.domain.entity.FileListEntry
import io.github.anvell.keemobile.domain.entity.FileSecrets
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.domain.usecase.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpenViewModel @Inject constructor(
    private val createNewFile: CreateNewFile,
    private val openFileSource: OpenFileSource,
    private val getRecentFiles: GetRecentFiles,
    private val saveRecentFiles: SaveRecentFiles,
    private val clearRecentFiles: ClearRecentFiles,
    private val getOpenDatabase: GetOpenDatabase
) : MviRxViewModel<OpenViewState>(OpenViewState()) {

    init {
        withState { state ->
            if (state.recentFiles is Uninitialized) {
                execute(getRecentFiles::invoke) {
                    copy(recentFiles = it, selectedFile = it()?.let { items ->
                        if (items.isEmpty()) null else items.last()
                    })
                }
            }
        }
    }

    fun createFile(source: FileSource, secrets: FileSecrets) {
        execute({ createNewFile(source, secrets) }) {
            copy(openFile = it)
        }
    }

    fun addFileSource(source: FileSource) {
        setState {
            val (recent, selected) = when (recentFiles) {
                is Success -> {
                    val entry = recentFiles()?.find { it.fileSource.id == source.id }
                    if (entry == null) {
                        recentFiles()!!.append(FileListEntry(source), AppConstants.MAX_RECENT_FILES) to FileListEntry(source)
                    } else {
                        recentFiles()!! to entry
                    }
                }
                else -> listOf(FileListEntry(source)) to FileListEntry(source)
            }
            persistRecentFiles(recent)
            copy(recentFiles = Success(recent), selectedFile = selected)
        }
    }

    fun selectFileSource(source: FileListEntry) {
        setState {
            copy(selectedFile = source)
        }
    }

    fun openFromSource(source: FileSource, secrets: FileSecrets) {
        execute({
            runCatching {
                getOpenDatabase(source.id).id
            }.getOrElse {
                openFileSource(source, secrets)
            }
        }) {
            copy(openFile = it)
        }
    }

    fun setInitialSetup(initialSetup: Boolean) {
        setState {
            copy(initialSetup = initialSetup)
        }
    }

    fun updateFileEntry(item: FileListEntry) = withState { state ->
        state.recentFiles()?.let { recent ->
            val items = recent.filter { it.fileSource.id != item.fileSource.id } + item
            execute({ saveRecentFiles(items) }) {
                copy(recentFiles = it)
            }
        }
    }

    fun pushRecentFiles() = withState { state ->
        setState {
            copy(recentFiles = Success(listOf()), recentFilesStash = state.recentFiles())
        }
    }

    fun popRecentFiles() = withState { state ->
        state.recentFilesStash?.let {
            setState {
                copy(recentFiles = Success(it), recentFilesStash = null)
            }
        }
    }

    fun clearRecentFiles() {
        execute(clearRecentFiles::invoke) {
            copy(recentFiles = Success(listOf()), selectedFile = null)
        }
    }

    private fun persistRecentFiles(recentFiles: List<FileListEntry>) {
        viewModelScope.launch {
            saveRecentFiles(recentFiles)
        }
    }
}
