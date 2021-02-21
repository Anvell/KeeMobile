package io.github.anvell.keemobile.presentation.open

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isGone
import androidx.core.widget.doAfterTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION
import dagger.hilt.android.AndroidEntryPoint
import io.github.anvell.keemobile.core.constants.Args
import io.github.anvell.keemobile.core.constants.RequestCodes
import io.github.anvell.keemobile.core.extensions.getName
import io.github.anvell.keemobile.core.extensions.toSha256
import io.github.anvell.keemobile.core.security.BiometricHelper
import io.github.anvell.keemobile.core.ui.extensions.*
import io.github.anvell.keemobile.core.ui.fragments.ViewBindingFragment
import io.github.anvell.keemobile.core.ui.mvi.MviRxView
import io.github.anvell.keemobile.core.ui.widgets.DividerDecoration
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.datatypes.Async
import io.github.anvell.keemobile.domain.datatypes.Fail
import io.github.anvell.keemobile.domain.datatypes.Loading
import io.github.anvell.keemobile.domain.datatypes.Success
import io.github.anvell.keemobile.domain.entity.FileListEntrySecrets
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.domain.entity.KeyOnly
import io.github.anvell.keemobile.domain.entity.Secret
import io.github.anvell.keemobile.presentation.R
import io.github.anvell.keemobile.presentation.databinding.FragmentOpenBinding
import io.github.anvell.keemobile.presentation.home.HomeViewModel
import io.github.anvell.keemobile.presentation.recentFile
import javax.inject.Inject

@AndroidEntryPoint
class OpenFragment : ViewBindingFragment<FragmentOpenBinding>(
    FragmentOpenBinding::inflate
), MviRxView<OpenViewModel, OpenViewState> {
    override val viewModel: OpenViewModel by viewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()

    @Inject
    lateinit var biometricHelper: BiometricHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDrawer()?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        with(requireBinding()) {
            dock.clipToCornerRadius(resources.getDimension(R.dimen.surface_corner_radius))
            recentFiles.clipToCornerRadius(resources.getDimension(R.dimen.surface_corner_radius))
            recentFiles.addItemDecoration(
                DividerDecoration(requireContext(), R.drawable.list_divider, LinearLayoutManager.VERTICAL)
            )

            fileCreate.setOnClickListener {
                requestCreateFile(getString(R.string.open_default_file_name), RequestCodes.FILE_CREATE)
            }
            fileOpen.setOnClickListener { requestOpenFile(RequestCodes.FILE_OPEN) }
            unlock.setOnClickListener { attemptUnlock() }
            clearAll.setOnClickListener { viewModel.pushRecentFiles() }
            password.doAfterTextChanged {
                viewModel.withState { state ->
                    unlock.isEnabled = password.text.isNotEmpty()
                            || state.selectedFile?.encryptedSecrets is FileListEntrySecrets.Some
                }
            }
            password.setOnEditorActionListener { _, action, _ ->
                if (action == EditorInfo.IME_ACTION_GO) {
                    attemptUnlock()
                    true
                } else {
                    false
                }
            }
        }
        initObservers()
        stateSubscribe(viewLifecycleOwner)
    }

    override fun onDetach() {
        viewModel.setInitialSetup(true)
        super.onDetach()
    }

    //TODO: Implement proper user flow for file creation
    override fun onFileCreated(uri: Uri) {
        val uriText = uri.toString()
        uri.getName(requireContext())?.let {
            viewModel.createFile(
                FileSource.Storage(uriText.toSha256(), it, uriText),
                KeyOnly(Secret.Unencrypted("123"))
            )
        }
    }

    override fun onFileOpened(uri: Uri) {
        val uriText = uri.toString()
        uri.getName(requireContext())?.let {
            viewModel.addFileSource(FileSource.Storage(uriText.toSha256(), it, uriText))
        }
    }

    override fun render(state: OpenViewState) {
        if (switchToOpenFile(state.openFile)) {
            return
        }

        val fileIsLoading = state.openFile is Loading
        setDockVisibility(!fileIsLoading)
        with(requireBinding()) {
            title.text = state.selectedFile?.fileSource?.nameWithoutExtension
            password.isEnabled = !fileIsLoading
            password.hint = if (state.selectedFile?.encryptedSecrets is FileListEntrySecrets.Some) {
                getString(R.string.open_text_hint_password_use_biometrics)
            } else {
                getString(R.string.open_text_hint_password)
            }
            unlock.isEnabled = state.selectedFile != null && !fileIsLoading
                    && (password.text.isNotEmpty()
                    || state.selectedFile.encryptedSecrets is FileListEntrySecrets.Some)
            clearAll.isEnabled = !fileIsLoading
        }

        val openIds = homeViewModel.withState { homeState ->
            homeState.openDatabases.map { it.source.id }
        }
        if (state.recentFiles is Success) {
            requireBinding().recentFiles.withModels {
                state.recentFiles()!!
                    .reversed()
                    .forEach { listEntry ->
                        val fileSource = listEntry.fileSource
                        recentFile {
                            id(fileSource.id)
                            title(fileSource.name)
                            isSelected(fileSource == state.selectedFile?.fileSource)
                            isClickable(!fileIsLoading)
                            isOpen(openIds.find { it == fileSource.id } != null)
                            isProcessing(fileIsLoading && fileSource == state.selectedFile?.fileSource)
                            clickListener(View.OnClickListener { viewModel.selectFileSource(listEntry) })
                        }
                    }
            }
        }

        if (state.recentFiles is Success || state.recentFiles is Fail) {
            // Use handler to ensure that view was laid out and width returns proper value
            Handler(Looper.getMainLooper()).post {
                switchPage(
                    state.recentFiles()?.isEmpty() ?: true, state.initialSetup
                )
            }
        }
    }

    private fun initObservers() {
        viewModel.selectSubscribe(OpenViewState::recentFilesStash)
            .observe(viewLifecycleOwner) { item ->
                item?.let {
                    snackbar(
                        getString(R.string.open_landing_popup_clear_all_info),
                        getString(R.string.open_button_undo),
                        action = {
                            viewModel.popRecentFiles()
                        },
                        onDismissed = { event ->
                            if (event != DISMISS_EVENT_ACTION) {
                                viewModel.clearRecentFiles()
                            }
                        })
                }
            }
        viewModel.selectSubscribe(OpenViewState::openFile)
            .observe(viewLifecycleOwner) { item ->
                if (item is Fail && !item.isConsumed) {
                    errorMapper.map(item.error) { snackbar(it) }
                }
            }
        setFragmentResultListener(UseFingerprintSheet.KEY) { _, bundle ->
            unlockSelected(
                bundle.getParcelable(Args.KEY) ?: error("Invalid arguments.")
            )
        }
    }

    private fun setDockVisibility(visible: Boolean) = with(requireBinding()) {
        val shiftY = if (visible) 0f else 100f.toPx()

        if (dock.translationY != shiftY) {
            dock.springAnimation(SpringAnimation.TRANSLATION_Y)
                .animateToFinalPosition(shiftY)
        }
    }

    private fun switchPage(isLanding: Boolean, performInitialSetup: Boolean = false) =
        with(requireBinding()) {
            val landingShiftX = if (isLanding) 0f else -openRoot.measuredWidth.toFloat()
            val recentFilesShiftX = if (isLanding) openRoot.measuredWidth.toFloat() else 0f

            if (performInitialSetup) {
                landingLayout.translationX = landingShiftX
                recentFilesLayout.translationX = recentFilesShiftX

                if (landingLayout.isGone) landingLayout.isGone = false
                if (recentFilesLayout.isGone) recentFilesLayout.isGone = false

                viewModel.setInitialSetup(false)
            } else {
                if (landingLayout.isGone) landingLayout.isGone = false
                if (recentFilesLayout.isGone) recentFilesLayout.isGone = false

                if (landingLayout.translationX != landingShiftX) {
                    landingLayout.springAnimation(SpringAnimation.TRANSLATION_X)
                        .animateToFinalPosition(landingShiftX)
                }

                if (recentFilesLayout.translationX != recentFilesShiftX) {
                    recentFilesLayout.springAnimation(SpringAnimation.TRANSLATION_X)
                        .animateToFinalPosition(recentFilesShiftX)
                }
            }
        }

    private fun switchToOpenFile(openFile: Async<VaultId>): Boolean {
        return when (openFile) {
            is Success -> {
                homeViewModel.switchDatabase(openFile())
                true
            }
            else -> false
        }
    }
}
