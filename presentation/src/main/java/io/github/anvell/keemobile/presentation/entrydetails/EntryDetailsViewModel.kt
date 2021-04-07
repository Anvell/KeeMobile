package io.github.anvell.keemobile.presentation.entrydetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.anvell.async.Fail
import io.github.anvell.async.Uninitialized
import io.github.anvell.either.*
import io.github.anvell.keemobile.core.extensions.getJsonArguments
import io.github.anvell.keemobile.core.ui.mvi.MviComposeViewModel
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.AppSettings
import io.github.anvell.keemobile.domain.entity.KeyAttachment
import io.github.anvell.keemobile.domain.usecase.GetAppSettings
import io.github.anvell.keemobile.domain.usecase.GetDatabaseEntry
import io.github.anvell.keemobile.domain.usecase.GetOpenDatabase
import io.github.anvell.keemobile.domain.usecase.SaveAttachment
import io.github.anvell.keemobile.presentation.data.EntryType
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EntryDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getOpenDatabase: GetOpenDatabase,
    private val getDatabaseEntry: GetDatabaseEntry,
    private val getAppSettings: GetAppSettings,
    private val saveAttachment: SaveAttachment
) : MviComposeViewModel<EntryDetailsViewState, EntryDetailsCommand>(
    EntryDetailsViewState(args = savedStateHandle.getJsonArguments())
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

    override fun onCommand(command: EntryDetailsCommand) {
        when (command) {
            is EntryDetailsCommand.SaveAttachment -> saveAttachmentFile(command.attachment)
        }
    }

    private fun loadAppSettings() = execute({
        getAppSettings().or { Right(AppSettings()) }
    }) {
        copy(appSettings = it)
    }

    private fun fetchDatabase(id: VaultId) {
        execute({ getOpenDatabase(id) }) {
            copy(activeDatabase = it)
        }
    }

    private fun fetchEntry() = withState { state ->
        execute({
            getDatabaseEntry(
                databaseId = state.activeDatabaseId,
                entryId = UUID.fromString(
                    when (state.entryType) {
                        is EntryType.Actual -> state.entryType.id
                        is EntryType.Historic -> state.entryType.id
                    }
                )
            )
        }) {
            copy(entry = it)
        }
    }

    private fun saveAttachmentFile(attachment: KeyAttachment) = withState { state ->
        val (name, ref) = attachment
        val binaryData = state.activeDatabase()?.database?.meta?.binaries?.find { it.id == ref }

        if (binaryData != null && !state.saveAttachmentQueue.contains(ref)) {
            setState { copy(saveAttachmentQueue = saveAttachmentQueue + ref) }

            viewModelScope.launch {
                saveAttachment(name, binaryData)
                    .mapLeft {
                        setState {
                            copy(
                                saveAttachmentQueue = state.saveAttachmentQueue - ref,
                                attachmentStatus = Fail(it)
                            )
                        }
                    }.map { uri ->
                        setState {
                            copy(
                                saveAttachmentQueue = state.saveAttachmentQueue - ref,
                                savedAttachments = state.savedAttachments + (ref to uri)
                            )
                        }
                    }
            }
        }
    }
}
