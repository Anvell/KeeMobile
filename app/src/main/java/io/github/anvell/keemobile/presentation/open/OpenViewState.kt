package io.github.anvell.keemobile.presentation.open

import com.airbnb.mvrx.MvRxState

data class OpenViewState(
    val openDatabase: String? = null
) : MvRxState
