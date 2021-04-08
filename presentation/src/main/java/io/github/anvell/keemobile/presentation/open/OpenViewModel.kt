package io.github.anvell.keemobile.presentation.open

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.anvell.async.Success
import io.github.anvell.either.Right
import io.github.anvell.either.map
import io.github.anvell.either.or
import io.github.anvell.keemobile.core.ui.mvi.MviComposeViewModel
import io.github.anvell.keemobile.domain.entity.FileListEntry
import io.github.anvell.keemobile.domain.entity.FileSecrets
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.domain.entity.VaultId
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
) : MviComposeViewModel<OpenViewState, OpenCommand>(OpenViewState()) {

    init {
        execute({
            getRecentFiles().or { Right(listOf()) }
        }) { files ->
            copy(
                recentFiles = files,
                selectedFile = files()?.lastOrNull()
            )
        }
    }

    override fun onCommand(command: OpenCommand) {
        when (command) {
            is OpenCommand.CreateFile -> createFile(command.source, command.secrets)
            is OpenCommand.SelectFile -> selectFileSource(command.source)
            is OpenCommand.AddFileSource -> addFileSource(command.source)
            is OpenCommand.OpenFile -> openFile(command.entry, command.secrets)
            is OpenCommand.RemoveRecentFile -> removeFileEntry(command.entry)
            is OpenCommand.ClearRecentFiles -> clearFiles()
        }
    }

    private fun createFile(source: FileSource, secrets: FileSecrets) {
        execute({ createNewFile(source, secrets) }) {
            copy(openFile = it)
        }
    }

    private fun addFileSource(source: FileSource) = withState { state ->
        val (recent, selected) = when (state.recentFiles) {
            is Success -> {
                val entry = state.recentFiles()?.find { it.fileSource.id == source.id }
                if (entry == null) {
                    val newItem = FileListEntry(source)
                    state.recentFiles.unwrap() + newItem to newItem
                } else {
                    state.recentFiles.unwrap() to entry
                }
            }
            else -> listOf(FileListEntry(source)) to FileListEntry(source)
        }

        execute({ saveRecentFiles(recent) }) {
            copy(recentFiles = Success(recent), selectedFile = selected)
        }
    }

    private fun selectFileSource(source: FileListEntry) {
        setState {
            copy(selectedFile = source)
        }
    }

    private fun openFile(
        entry: FileListEntry,
        secrets: FileSecrets
    ) {
        updateFileEntry(entry)

        execute({
            getOpenDatabase(VaultId(entry.fileSource.id))
                .or { openFileSource(entry.fileSource, secrets) }
                .map { it.id }
        }) {
            copy(openFile = it)
        }
    }

    private fun updateFileEntry(item: FileListEntry) = withState { state ->
        val items = state.recentFiles
            .unwrap()
            .filter { it.fileSource.id != item.fileSource.id }
            .plus(item)

        viewModelScope.launch {
            saveRecentFiles(items).map {
                setState {
                    copy(recentFiles = Success(it))
                }
            }
        }
    }

    private fun removeFileEntry(item: FileListEntry) = withState { state ->
        val items = state.recentFiles.unwrap() - item

        viewModelScope.launch {
            saveRecentFiles(items).map {
                setState {
                    copy(recentFiles = Success(it))
                }
            }
        }
    }

    private fun clearFiles() = execute({ clearRecentFiles() }) {
        copy(
            recentFiles = Success(listOf()),
            selectedFile = null
        )
    }
}
