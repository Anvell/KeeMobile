package io.github.anvell.keemobile.presentation.home

import android.os.Bundle
import android.view.WindowManager.LayoutParams
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import io.github.anvell.keemobile.R
import io.github.anvell.keemobile.common.extensions.injector
import io.github.anvell.keemobile.databinding.ActivityHomeBinding
import io.github.anvell.keemobile.presentation.base.BaseActivity
import javax.inject.Inject

class HomeActivity : BaseActivity(), DrawerHolder, NavControllerHolder {

    @Inject
    lateinit var viewModelFactory: HomeViewModel.Factory

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE)
        injector.inject(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
    }

    override fun getDrawer(): DrawerLayout {
        return binding.drawer
    }

    override fun getNavController(): NavController {
        return findNavController(R.id.nav_host_fragment)
    }
}
