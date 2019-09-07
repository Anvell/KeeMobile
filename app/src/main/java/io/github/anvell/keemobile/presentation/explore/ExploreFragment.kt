package io.github.anvell.keemobile.presentation.explore

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import io.github.anvell.keemobile.R
import io.github.anvell.keemobile.common.extensions.addDivider
import io.github.anvell.keemobile.common.extensions.injector
import io.github.anvell.keemobile.databinding.FragmentExploreBinding
import io.github.anvell.keemobile.itemEntry
import io.github.anvell.keemobile.presentation.base.BaseFragment
import java.util.*
import javax.inject.Inject

@SuppressLint("ClickableViewAccessibility")
class ExploreFragment : BaseFragment<FragmentExploreBinding>(FragmentExploreBinding::inflate) {

    @Inject
    lateinit var viewModelFactory: ExploreViewModel.Factory

    private val viewModel: ExploreViewModel by fragmentViewModel()

    override fun onAttach(context: Context) {
        requireActivity().injector.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.exploreView.addDivider(requireContext(), R.drawable.list_divider, LinearLayoutManager.VERTICAL)
        binding.search.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                hideSoftKeyboard()
                binding.searchExtraButton.playReverse()
            } else {
                binding.searchExtraButton.play()
            }
        }
        binding.exploreRoot.setOnTouchListener { _, _ ->
            hideSoftKeyboard()
            binding.search.clearFocus()
            false
        }

        binding.navigateButton.setOnClickListener {
            updateUiOnNavigation(false)
            withState(viewModel) { state ->
                if (state.rootStack.isEmpty()) {
                    // TODO: Show drawer
                } else {
                    viewModel.navigateUp()
                }
            }
        }
    }

    override fun onBackPressed() {
        updateUiOnNavigation(false)
        withState(viewModel) { state ->
            if (state.rootStack.isEmpty()) {
                super.onBackPressed()
            } else {
                viewModel.navigateUp()
            }
        }
    }

    override fun invalidate(): Unit = withState(viewModel) { state ->

        if (state.activeDatabase is Success) {

            state.activeDatabase()?.database?.let { db ->

                val group = if (state.rootStack.isEmpty()) db.root else {
                    db.findGroup { it.uuid == state.rootStack.last() }
                }

                binding.exploreView.withModels {

                    group?.groups?.forEach { entry ->
                        itemEntry {
                            id(entry.uuid.toString())
                            title(entry.name)
                            subtitle(entry.notes)
                            iconId(R.drawable.ic_folder)
                            isSelected(false)
                            clickListener(View.OnClickListener { onGroupClicked(entry.uuid) })
                        }
                    }

                    group?.entries?.forEach { entry ->
                        itemEntry {
                            id(entry.uuid.toString())
                            title(entry.title)
                            subtitle(entry.username)
                            iconId(R.drawable.ic_plus_square)
                            isSelected(false)
                            clickListener(View.OnClickListener { })
                        }
                    }
                }
            }

        }
    }

    private fun onGroupClicked(id: UUID) {
        updateUiOnNavigation(true)
        viewModel.activateGroup(id)
    }

    private fun updateUiOnNavigation(isForward: Boolean) {
        withState(viewModel) { state ->
            if (isForward) {
                if (state.rootStack.isEmpty()) {
                    binding.navigateButton.play()
                }
            } else {
                if (state.rootStack.size == 1) {
                    binding.navigateButton.playReverse()
                }
            }
        }
    }

}
