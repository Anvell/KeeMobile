package io.github.anvell.keemobile.presentation.open

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.anvell.keemobile.common.constants.AppConstants
import io.github.anvell.keemobile.common.extensions.append
import io.github.anvell.keemobile.domain.entity.FileSecrets
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.domain.usecase.CreateNewFile
import io.github.anvell.keemobile.domain.usecase.OpenFileSource
import io.github.anvell.keemobile.presentation.base.BaseViewModel

class OpenViewModel @AssistedInject constructor(
    @Assisted initialState: OpenViewState,
    private val createNewFile: CreateNewFile,
    private val openFileSource: OpenFileSource
) : BaseViewModel<OpenViewState>(initialState) {

    fun createFile(source: FileSource, secrets: FileSecrets) {
        createNewFile
            .use(source, secrets)
            .execute {
                copy(opened = it)
            }
    }

    fun addFileSource(source: FileSource) {
        setState {
            val entry = recentFiles.find { it.id == source.id }
            if(entry == null) {
                copy(recentFiles = recentFiles.append(source, AppConstants.MAX_RECENT_FILES), selectedFile = source)
            } else {
                copy(selectedFile = entry)
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
