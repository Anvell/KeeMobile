package io.github.anvell.keemobile.core.ui.navigation

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData

enum class TransitionAnimation {
    None, Slide, Push, Fade
}

interface AppNavigator {
    fun popBackStack(): Boolean

    fun navigateToStartDestination(
        transition: TransitionAnimation = TransitionAnimation.None
    )

    fun navigate(
        @IdRes id: Int,
        data: Any? = null
    )

    fun navigate(
        deeplinkUri: String,
        transition: TransitionAnimation = TransitionAnimation.None,
        clearBackstack: Boolean = false
    )

    fun <T> currentDestinationResult(): LiveData<T?>

    fun <T> currentDestinationResult(initialValue: T): LiveData<T>

    fun setDestinationResult(data: Any?)
}
