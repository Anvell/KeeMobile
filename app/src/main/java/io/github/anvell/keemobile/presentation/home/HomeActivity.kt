package io.github.anvell.keemobile.presentation.home

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import io.github.anvell.keemobile.R
import io.github.anvell.keemobile.common.extensions.injector
import io.github.anvell.keemobile.databinding.ActivityHomeBinding
import io.github.anvell.keemobile.presentation.base.BaseActivity
import javax.inject.Inject

class HomeActivity : BaseActivity(), DrawerHolder {

    @Inject
    lateinit var viewModelFactory: HomeViewModel.Factory

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
    }

    override fun getDrawer(): DrawerLayout {
        return binding.drawer
    }
}
