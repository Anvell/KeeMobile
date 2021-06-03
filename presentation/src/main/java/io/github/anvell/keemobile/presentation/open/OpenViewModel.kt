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
import kotlinx.coroutines.async
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
        viewModelScope.async {
            getRecentFiles().or { Right(listOf()) }
        }.reduceAsState { files ->
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
            is OpenCommand.AddKeyFile -> withState { state ->
                state.selectedFile?.also {
                    updateFileEntry(it.copy(keyFile = command.keyFile))
                }
            }
            is OpenCommand.RemoveKeyFile -> withState { state ->
                state.selectedFile?.also {
                    updateFileEntry(it.copy(keyFile = null))
                }
            }
        }
    }

    private fun createFile(source: FileSource, secrets: FileSecrets) {
        viewModelScope.async {
            createNewFile(source, secrets)
        }.reduceAsState {
            copy(openFile = it)
        }
    }

    private fun addFileSource(source: FileSource) = withState { state ->
        val (recent, selected) = when (state.recentFiles) {
            is Success -> {
                val entry = state.recentFiles()?.find { it.vault.id == source.id }
                if (entry == null) {
                    val newItem = FileListEntry(source)
                    state.recentFiles.unwrap() + newItem to newItem
                } else {
                    state.recentFiles.unwrap() to entry
                }
            }
            else -> listOf(FileListEntry(source)) to FileListEntry(source)
        }

        viewModelScope.async {
            saveRecentFiles(recent)
        }.reduceAsState {
            copy(
                recentFiles = Success(recent),
                selectedFile = selected
            )
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

        viewModelScope.async {
            getOpenDatabase(VaultId(entry.vault.id))
                .or { openFileSource(entry.vault, secrets) }
                .map { it.id }
        }.reduceAsState {
            copy(openFile = it)
        }
    }

    private fun updateFileEntry(item: FileListEntry) = withState { state ->
        val items = state.recentFiles
            .unwrap()
            .filter { it.vault.id != item.vault.id }
            .plus(item)

        viewModelScope.launch {
            saveRecentFiles(items).map {
                setState {
                    copy(
                        recentFiles = Success(it),
                        selectedFile = item
                    )
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

    private fun clearFiles() {
        viewModelScope.async {
            clearRecentFiles()
        }.reduceAsState {
            copy(
                recentFiles = Success(listOf()),
                selectedFile = null
            )
        }
    }
}
