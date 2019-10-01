package io.github.anvell.keemobile.presentation.drawer

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import io.github.anvell.keemobile.common.extensions.injector
import io.github.anvell.keemobile.databinding.FragmentDrawerBinding
import io.github.anvell.keemobile.presentation.base.BaseFragment
import io.github.anvell.keemobile.presentation.home.HomeViewModel

class DrawerFragment : BaseFragment<FragmentDrawerBinding>(FragmentDrawerBinding::inflate) {

    private val viewModel: HomeViewModel by activityViewModel()

    override fun onAttach(context: Context) {
        requireActivity().injector.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onBackPressed() {
        if(getDrawer()?.isDrawerOpen(GravityCompat.START) == true) {
            getDrawer()?.closeDrawer(GravityCompat.START)
        } else {
            activity?.onBackPressed()
        }
    }

    override fun invalidate(): Unit = withState(viewModel) { state ->

    }

}
