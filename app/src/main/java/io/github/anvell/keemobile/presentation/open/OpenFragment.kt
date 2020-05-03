package io.github.anvell.keemobile.presentation.open

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isGone
import androidx.drawerlayout.widget.DrawerLayout
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.*
import com.google.android.material.snackbar.BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION
import io.github.anvell.keemobile.R
import io.github.anvell.keemobile.common.extensions.*
import io.github.anvell.keemobile.databinding.FragmentOpenBinding
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.FileSecrets
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.itemRecentFile
import io.github.anvell.keemobile.presentation.base.BaseFragment
import io.github.anvell.keemobile.presentation.home.HomeViewModel
import io.github.anvell.keemobile.presentation.widgets.DividerDecoration
import javax.inject.Inject

class OpenFragment : BaseFragment<FragmentOpenBinding>(FragmentOpenBinding::inflate) {

    @Inject
    lateinit var viewModelFactory: OpenViewModel.Factory

    private val viewModel: OpenViewModel by fragmentViewModel()
    private val homeViewModel: HomeViewModel by activityViewModel()

    override fun onAttach(context: Context) {
        requireActivity().injector.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDrawer()?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        binding.dock.clipToCornerRadius(resources.getDimension(R.dimen.surface_corner_radius))
        binding.recentFiles.clipToCornerRadius(resources.getDimension(R.dimen.surface_corner_radius))
        binding.recentFiles.addItemDecoration(
            DividerDecoration(requireContext(), R.drawable.list_divider, LinearLayoutManager.VERTICAL)
        )

        binding.fileCreate.setOnClickListener { requestCreateFile(getString(R.string.default_file_name)) }
        binding.fileOpen.setOnClickListener { requestOpenFile() }
        binding.unlock.setOnClickListener { unlockSelected() }
        binding.clearAll.setOnClickListener { viewModel.pushRecentFiles() }
        binding.password.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_GO) {
                unlockSelected()
            } else {
                false
            }
        }

        initObservers()
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
                FileSecrets("123")
            )
        }
    }

    override fun onFileOpened(uri: Uri) {
        val uriText = uri.toString()
        uri.getName(requireContext())?.let {
            viewModel.addFileSource(FileSource.Storage(uriText.toSha256(), it, uriText))
        }
    }

    override fun invalidate() = withState(viewModel) { state ->
        if (switchToOpenFile(state.openFile)) {
            return@withState
        }

        val fileIsLoading = state.openFile is Loading
        binding.title.text = state.selectedFile?.nameWithoutExtension
        binding.unlock.isEnabled = state.selectedFile != null && !fileIsLoading
        binding.password.isEnabled = !fileIsLoading
        binding.clearAll.isEnabled = !fileIsLoading
        setDockVisibility(!fileIsLoading)

        val openIds = withState(homeViewModel) { homeState ->
            homeState.openDatabases.map { it.source.id }
        }
        if (state.recentFiles is Success) {
            binding.recentFiles.withModels {
                state.recentFiles()!!
                    .reversed()
                    .forEach { entry ->
                        itemRecentFile {
                            id(entry.id)
                            title(entry.name)
                            isSelected(entry == state.selectedFile)
                            isClickable(!fileIsLoading)
                            isOpen(openIds.find { it == entry.id } != null)
                            isProcessing(fileIsLoading && entry == state.selectedFile)
                            clickListener(View.OnClickListener { viewModel.selectFileSource(entry) })
                        }
                    }
            }
        }

        if (state.recentFiles is Success || state.recentFiles is Fail) {
            switchPage(state.recentFiles()?.isEmpty() ?: true, state.initialSetup)
        }
    }

    private fun initObservers() {
        viewModel.selectSubscribe(viewLifecycleOwner,
            OpenViewState::recentFilesStash,
            deliveryMode = uniqueOnly()) {
            it?.let {
                snackbar(
                    getString(R.string.landing_popup_clear_all_info),
                    getString(R.string.button_undo),
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
        snackbarOnFailedState(viewModel, OpenViewState::openFile)
    }

    private fun setDockVisibility(visible: Boolean) {
        val shiftY = if (visible) 0f else 100f.toPx()

        if (binding.dock.translationY != shiftY) {
            binding.dock.springAnimation(SpringAnimation.TRANSLATION_Y)
                .animateToFinalPosition(shiftY)
        }
    }

    private fun switchPage(isLanding: Boolean, performInitialSetup: Boolean = false) {
        val landingShiftX = if (isLanding) 0f else -binding.openRoot.measuredWidth.toFloat()
        val recentFilesShiftX = if (isLanding) binding.openRoot.measuredWidth.toFloat() else 0f

        if (performInitialSetup) {
            binding.landingLayout.translationX = landingShiftX
            binding.recentFilesLayout.translationX = recentFilesShiftX

            if (binding.landingLayout.isGone) binding.landingLayout.isGone = false
            if (binding.recentFilesLayout.isGone) binding.recentFilesLayout.isGone = false

            viewModel.setInitialSetup(false)
        } else {
            if (binding.landingLayout.isGone) binding.landingLayout.isGone = false
            if (binding.recentFilesLayout.isGone) binding.recentFilesLayout.isGone = false

            if (binding.landingLayout.translationX != landingShiftX) {
                binding.landingLayout.springAnimation(SpringAnimation.TRANSLATION_X)
                    .animateToFinalPosition(landingShiftX)
            }

            if (binding.recentFilesLayout.translationX != recentFilesShiftX) {
                binding.recentFilesLayout.springAnimation(SpringAnimation.TRANSLATION_X)
                    .animateToFinalPosition(recentFilesShiftX)
            }
        }
    }

    private fun unlockSelected(): Boolean = withState(viewModel) { state ->
        val selected = state.selectedFile
        return@withState if (binding.password.text.isNotEmpty() && selected != null) {
            viewModel.openFromSource(selected, FileSecrets(binding.password.text.toString()))
            true
        } else {
            false
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
