package io.github.anvell.keemobile.presentation.home

import android.os.Bundle
import android.view.WindowManager.LayoutParams
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.anvell.keemobile.core.permissions.PermissionsProvider
import io.github.anvell.keemobile.core.permissions.PermissionsProxy
import io.github.anvell.keemobile.core.ui.ativities.BaseActivity
import io.github.anvell.keemobile.core.ui.ativities.DrawerHolder
import io.github.anvell.keemobile.core.ui.ativities.NavControllerHolder
import io.github.anvell.keemobile.presentation.R
import io.github.anvell.keemobile.presentation.databinding.ActivityHomeBinding
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity(), DrawerHolder, NavControllerHolder, PermissionsProxy {
    private lateinit var binding: ActivityHomeBinding

    @Inject
    lateinit var permissionsProvider: PermissionsProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
    }

    override fun onStart() {
        permissionsProvider.registerProxy(this)
        super.onStart()
    }

    override fun onStop() {
        permissionsProvider.unregisterProxy()
        super.onStop()
    }

    override fun getDrawer(): DrawerLayout {
        return binding.drawer
    }

    override fun getNavController(): NavController {
        return findNavController(R.id.nav_host_fragment)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsProvider.permissionsProcessed(
            requestCode,
            permissions.toList(),
            grantResults.toList()
        )
    }
}
