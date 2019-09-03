package io.github.anvell.keemobile.presentation.explore

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import io.github.anvell.keemobile.R
import io.github.anvell.keemobile.common.extensions.addDivider
import io.github.anvell.keemobile.common.extensions.injector
import io.github.anvell.keemobile.databinding.FragmentExploreBinding
import io.github.anvell.keemobile.itemEntry
import io.github.anvell.keemobile.presentation.base.BaseFragment
import timber.log.Timber
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

        binding.navigateButton.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                if (fragmentManager!!.backStackEntryCount > 0)
                    R.drawable.ic_arrow_back else R.drawable.ic_menu
            )
        )

        binding.navigateButton.setOnClickListener {
            if (fragmentManager!!.backStackEntryCount > 0) {
                findNavController().navigateUp()
            }
        }
    }

    override fun invalidate(): Unit = withState(viewModel) { state ->

        if (state.activeDatabase is Success && state.activeRoot != null) {

            state.activeDatabase()?.database?.let { db ->

                val group = db.findGroup { it.uuid == state.activeRoot }

                binding.exploreView.withModels {

                    group?.groups?.forEach { entry ->
                        itemEntry {
                            id(entry.uuid.toString())
                            title(entry.name)
                            subtitle(entry.notes)
                            iconId(R.drawable.ic_folder)
                            isSelected(false)
                            clickListener(View.OnClickListener { navigateToGroup(entry.uuid) })
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

    private fun navigateToGroup(groupId: UUID) {
        withState(viewModel) { state ->
            try {
                findNavController().navigate(R.id.exploreFragment,
                    bundleOf(MvRx.KEY_ARG to ExploreArgs(state.activeDatabaseId, groupId))
                )
            } catch (e: IllegalArgumentException) {
                Timber.d(e)
            }
        }
    }

}
