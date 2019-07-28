package io.github.anvell.keemobile.presentation.home

import com.airbnb.mvrx.MvRxState

data class HomeViewState(
    val openDatabase: String? = null
) : MvRxState
