package io.github.anvell.keemobile.presentation.entry

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import io.github.anvell.keemobile.common.extensions.getArguments
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.AppSettings
import io.github.anvell.keemobile.domain.entity.KeyAttachment
import io.github.anvell.keemobile.domain.entity.Uninitialized
import io.github.anvell.keemobile.domain.exceptions.EntryNotFoundException
import io.github.anvell.keemobile.domain.usecase.GetAppSettings
import io.github.anvell.keemobile.domain.usecase.GetOpenDatabase
import io.github.anvell.keemobile.domain.usecase.SaveAttachment
import io.github.anvell.keemobile.presentation.base.MviViewModel
import timber.log.Timber
import java.util.concurrent.TimeUnit

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
            if(state.activeDatabase is Uninitialized) {
                fetchDatabase(state.activeDatabaseId)
            }

            if(state.entry is Uninitialized) {
                fetchEntry()
            }
        }

        loadAppSettings()
    }

    private fun loadAppSettings() {
        getAppSettings
            .use()
            .onErrorReturnItem(AppSettings())
            .doOnError(Timber::d)
            .execute {
                copy(appSettings = it)
            }
    }

    fun fetchDatabase(id: VaultId) {
        getOpenDatabase
            .use(id)
            .doOnError(Timber::d)
            .execute {
                copy(activeDatabase = it)
            }
    }

    fun fetchEntry() = withState { state ->
        getOpenDatabase
            .use(state.activeDatabaseId)
            .map {
                it.database.findEntry { item ->
                    item.uuid == state.entryId
                }?.second ?: throw EntryNotFoundException()
            }
            .doOnError(Timber::d)
            .execute {
                copy(entry = it)
            }
    }

    fun saveAttachmentFile(attachment: KeyAttachment) = withState { state ->
        val (name, ref) = attachment
        val binaryData = state.activeDatabase()?.database?.meta?.binaries?.find { it.id == ref }

        if (binaryData != null && !state.saveAttachmentQueue.contains(ref)) {
            setState { copy(saveAttachmentQueue = saveAttachmentQueue + ref) }

            saveAttachment
                .use(name, binaryData)
                .delay(300, TimeUnit.MILLISECONDS)
                .doOnError(Timber::d)
                .subscribe({ uri ->
                    setState {
                        copy(
                            saveAttachmentQueue = saveAttachmentQueue - ref,
                            savedAttachments = savedAttachments + (ref to uri)
                        )
                    }
                }, { error ->
                    setState {
                        copy(saveAttachmentQueue = saveAttachmentQueue - ref, errorSink = error)
                    }
                })
        }
    }
}
