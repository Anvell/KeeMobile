package io.github.anvell.keemobile.presentation.home

import android.os.Bundle
import android.view.WindowManager.LayoutParams
import dagger.hilt.android.AndroidEntryPoint
import io.github.anvell.keemobile.core.ui.ativities.BaseActivity
import io.github.anvell.keemobile.presentation.R

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE)

        setContentView(R.layout.activity_home)
    }
}
