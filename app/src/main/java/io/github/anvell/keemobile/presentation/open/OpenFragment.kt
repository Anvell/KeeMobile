package io.github.anvell.keemobile.presentation.open

import android.content.Context
import com.airbnb.mvrx.fragmentViewModel
import io.github.anvell.keemobile.extensions.injector
import io.github.anvell.keemobile.presentation.core.BaseFragment
import io.github.anvell.keemobile.presentation.home.HomeViewModel
import javax.inject.Inject

class OpenFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: OpenViewModel.Factory

    private val viewModel: OpenViewModel by fragmentViewModel()

    override fun onAttach(context: Context) {
        requireActivity().injector.inject(this)
        super.onAttach(context)
    }

    override fun invalidate() {
    }

}
