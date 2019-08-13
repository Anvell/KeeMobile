package io.github.anvell.keemobile.presentation.explore

import android.content.Context
import android.os.Bundle
import android.view.View
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import io.github.anvell.keemobile.databinding.FragmentExploreBinding
import io.github.anvell.keemobile.extensions.injector
import io.github.anvell.keemobile.presentation.core.BaseFragment
import javax.inject.Inject

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

    }

    override fun invalidate() = withState(viewModel) { state ->

    }

}
