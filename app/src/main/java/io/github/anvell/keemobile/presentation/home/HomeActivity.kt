package io.github.anvell.keemobile.presentation.home

import android.os.Bundle
import io.github.anvell.keemobile.R
import io.github.anvell.keemobile.common.extensions.injector
import io.github.anvell.keemobile.presentation.core.BaseActivity
import javax.inject.Inject

class HomeActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: HomeViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        setContentView(R.layout.activity_home)
    }
}
