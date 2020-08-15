package io.github.anvell.keemobile.presentation.entry

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import io.github.anvell.keemobile.core.extensions.getArguments
import io.github.anvell.keemobile.core.ui.mvi.MviViewModel
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.AppSettings
import io.github.anvell.keemobile.domain.entity.KeyAttachment
import io.github.anvell.keemobile.domain.datatypes.Uninitialized
import io.github.anvell.keemobile.domain.exceptions.EntryNotFoundException
import io.github.anvell.keemobile.domain.usecase.GetAppSettings
import io.github.anvell.keemobile.domain.usecase.GetOpenDatabase
import io.github.anvell.keemobile.domain.usecase.SaveAttachment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EntryDetailsViewModel @ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val getOpenDatabase: GetOpenDatabase,
    private val getAppSettings: GetAppSettings,
    private val saveAttachment: SaveAttachment
) : MviViewModel<EntryDetailsViewState>(
    EntryDetailsViewState(args = savedStateHandle.getArguments())
) {

    init {
        withState { state ->
            if (state.activeDatabase is Uninitialized) {
                fetchDatabase(state.activeDatabaseId)
            }

            if (state.entry is Uninitialized) {
                fetchEntry()
            }
        }

        loadAppSettings()
    }

    private fun loadAppSettings() = execute({
        runCatching {
            getAppSettings()
        }.getOrElse {
            AppSettings()
        }
    }) {
        copy(appSettings = it)
    }

    fun fetchDatabase(id: VaultId) {
        execute({ getOpenDatabase(id) }) {
            copy(activeDatabase = it)
        }
    }

    fun fetchEntry() = withState { state ->
        execute({
            getOpenDatabase(state.activeDatabaseId).let {
                it.database.findEntry { item ->
                    item.uuid == state.entryId
                }?.second ?: throw EntryNotFoundException()
            }
        }) {
            copy(entry = it)
        }
    }

    fun saveAttachmentFile(attachment: KeyAttachment) = withState { state ->
        val (name, ref) = attachment
        val binaryData = state.activeDatabase()?.database?.meta?.binaries?.find { it.id == ref }

        if (binaryData != null && !state.saveAttachmentQueue.contains(ref)) {
            setState { copy(saveAttachmentQueue = saveAttachmentQueue + ref) }

            viewModelScope.launch {
                delay(300)
                runCatching {
                    saveAttachment(name, binaryData)
                }.fold(
                    onSuccess = { uri ->
                        setState {
                            copy(
                                saveAttachmentQueue = saveAttachmentQueue - ref,
                                savedAttachments = savedAttachments + (ref to uri)
                            )
                        }
                    },
                    onFailure = { error ->
                        setState {
                            copy(saveAttachmentQueue = saveAttachmentQueue - ref, errorSink = error)
                        }
                    }
                )
            }
        }
    }
}
