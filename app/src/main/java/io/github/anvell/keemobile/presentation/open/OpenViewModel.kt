package io.github.anvell.keemobile.presentation.open

import com.airbnb.mvrx.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.anvell.keemobile.common.constants.AppConstants
import io.github.anvell.keemobile.common.extensions.append
import io.github.anvell.keemobile.domain.entity.FileSecrets
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.domain.usecase.CreateNewFile
import io.github.anvell.keemobile.domain.usecase.GetRecentFiles
import io.github.anvell.keemobile.domain.usecase.OpenFileSource
import io.github.anvell.keemobile.domain.usecase.SaveRecentFiles
import io.github.anvell.keemobile.presentation.base.BaseViewModel
import timber.log.Timber

class OpenViewModel @AssistedInject constructor(
    @Assisted initialState: OpenViewState,
    private val createNewFile: CreateNewFile,
    private val openFileSource: OpenFileSource,
    private val getRecentFiles: GetRecentFiles,
    private val saveRecentFiles: SaveRecentFiles
) : BaseViewModel<OpenViewState>(initialState) {

    init {
        withState { state ->
            if (state.recentFiles is Uninitialized) {
                getRecentFiles
                    .use()
                    .execute {
                        copy(recentFiles = it, selectedFile = it()?.last())
                    }
            }
        }
    }

    fun createFile(source: FileSource, secrets: FileSecrets) {
        createNewFile
            .use(source, secrets)
            .execute {
                copy(opened = it)
            }
    }

    fun addFileSource(source: FileSource) {
        setState {
            when (recentFiles) {
                is Success -> {
                    val entry = recentFiles()?.find { it.id == source.id }
                    if(entry == null) {
                        recentFiles()!!.append(source, AppConstants.MAX_RECENT_FILES).let {
                            saveRecentFiles(it)
                            copy(recentFiles = Success(it), selectedFile = source)
                        }

                    } else {
                        copy(selectedFile = entry)
                    }
                }
                else -> {
                    listOf(source).let {
                        saveRecentFiles(it)
                        copy(recentFiles = Success(it), selectedFile = source)
                    }
                }
            }
        }
    }

    fun selectFileSource(source: FileSource) {
        setState {
            copy(selectedFile = source)
        }

    }

    fun openFromSource(source: FileSource, secrets: FileSecrets) {
        openFileSource
            .use(source, secrets)
            .execute {
                copy(opened = it)
            }
    }

    private fun saveRecentFiles(recentFiles: List<FileSource>) {
        disposables.add(saveRecentFiles
            .use(recentFiles)
            .onErrorReturn { Timber.d(it) }
            .subscribe()
        )
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: OpenViewState): OpenViewModel
    }

    companion object : MvRxViewModelFactory<OpenViewModel, OpenViewState> {
        override fun create(viewModelContext: ViewModelContext, state: OpenViewState): OpenViewModel? {
            val fragment: OpenFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.viewModelFactory.create(state)
        }
    }
}
