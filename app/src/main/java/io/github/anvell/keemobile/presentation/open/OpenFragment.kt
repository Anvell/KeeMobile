package io.github.anvell.keemobile.presentation.open

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.*
import io.github.anvell.keemobile.R
import io.github.anvell.keemobile.common.extensions.*
import io.github.anvell.keemobile.databinding.FragmentOpenBinding
import io.github.anvell.keemobile.domain.entity.FileSecrets
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.itemRecentFile
import io.github.anvell.keemobile.presentation.core.BaseFragment
import io.github.anvell.keemobile.presentation.explore.ExploreArgs
import java.util.*
import javax.inject.Inject

class OpenFragment : BaseFragment<FragmentOpenBinding>(FragmentOpenBinding::inflate) {

    @Inject
    lateinit var viewModelFactory: OpenViewModel.Factory

    private val viewModel: OpenViewModel by fragmentViewModel()

    override fun onAttach(context: Context) {
        requireActivity().injector.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dock.clipToCornerRadius(resources.getDimension(R.dimen.surface_corner_radius))
        binding.recentFiles.clipToCornerRadius(resources.getDimension(R.dimen.surface_corner_radius))
        binding.recentFiles.addDivider(requireContext(), R.drawable.list_divider, LinearLayoutManager.VERTICAL)

        binding.fileCreate.setOnClickListener { }
        binding.fileOpen.setOnClickListener { requestOpenFile() }
        binding.unlock.setOnClickListener {
            withState(viewModel) { state ->
                state.selectedFile?.let {
                    viewModel.openFromSource(state.selectedFile, FileSecrets(binding.password.text.toString()))
                }
            }
        }

        snackbarOnFailedState(viewModel, OpenViewState::opened)
    }

    override fun onFileOpened(uri: Uri) {
        val uriText = uri.toString()
        viewModel.addFileSource(
            FileSource.Storage(uriText.toSha256(), uri.getName(requireContext()) ?: "", uriText)
        )
    }

    override fun invalidate() = withState(viewModel) { state ->

        handleOpening(state.opened)

        binding.title.text = state.selectedFile?.name
        binding.unlock.isEnabled = state.selectedFile != null

        if(state.recentFiles.isEmpty()) {
            binding.motionLayout.transitionToStart()
        } else {
            binding.recentFiles.withModels {
                state.recentFiles.reversed().forEach { entry ->

                    if(entry is FileSource.Storage) {
                        itemRecentFile {
                            id(entry.id)
                            title(entry.name)
                            isSelected(entry == state.selectedFile)
                            clickListener(View.OnClickListener { viewModel.selectFileSource(entry) })
                        }
                    }
                }
            }
            binding.recentFiles.requestModelBuild()
            binding.motionLayout.transitionToEnd()
        }
    }

    private fun handleOpening(opened: Async<UUID>) {
        when(opened) {
            is Success -> findNavController().navigate(R.id.action_explore_database, bundleOf(MvRx.KEY_ARG to ExploreArgs(opened())))
        }
    }

}
