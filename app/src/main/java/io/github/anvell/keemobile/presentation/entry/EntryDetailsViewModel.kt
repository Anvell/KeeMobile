package io.github.anvell.keemobile.presentation.entry

import com.airbnb.mvrx.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.AppSettings
import io.github.anvell.keemobile.domain.exceptions.EntryNotFoundException
import io.github.anvell.keemobile.domain.usecase.GetAppSettings
import io.github.anvell.keemobile.domain.usecase.GetOpenDatabase
import io.github.anvell.keemobile.presentation.base.BaseViewModel
import timber.log.Timber

class EntryDetailsViewModel @AssistedInject constructor(
    @Assisted initialState: EntryDetailsViewState,
    private val getOpenDatabase: GetOpenDatabase,
    private val getAppSettings: GetAppSettings
) : BaseViewModel<EntryDetailsViewState>(initialState) {

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

    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: EntryDetailsViewState): EntryDetailsViewModel
    }

    companion object : MvRxViewModelFactory<EntryDetailsViewModel, EntryDetailsViewState> {
        override fun create(viewModelContext: ViewModelContext, state: EntryDetailsViewState): EntryDetailsViewModel? {
            val fragment: EntryDetailsFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.viewModelFactory.create(state)
        }
    }
}
