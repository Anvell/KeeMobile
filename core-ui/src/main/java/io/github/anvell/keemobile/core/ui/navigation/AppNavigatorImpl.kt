package io.github.anvell.keemobile.core.ui.navigation

import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import io.github.anvell.keemobile.core.constants.Args
import io.github.anvell.keemobile.core.ui.navigation.*

class AppNavigatorImpl(private val controller: NavController) : AppNavigator {

    override fun popBackStack() = controller.popBackStack()

    override fun navigateToStartDestination(transition: TransitionAnimation) {
        controller.navigateToStartDestination {
            when (transition) {
                TransitionAnimation.None -> {}
                TransitionAnimation.Slide -> anim { slideFromSide() }
                TransitionAnimation.Push -> anim { pushUp() }
                TransitionAnimation.Fade -> anim { fadeIn() }
            }
        }
    }

    override fun navigate(@IdRes id: Int, data: Any?) {
        if (data != null) {
            controller.navigate(id, bundleOf(Args.KEY to data))
        } else {
            controller.navigate(id)
        }
    }

    override fun navigate(
        deeplinkUri: String,
        transition: TransitionAnimation,
        clearBackstack: Boolean
    ) {
        controller.navigate(deeplinkUri, clearBackstack) {
            when (transition) {
                TransitionAnimation.None -> {}
                TransitionAnimation.Slide -> anim { slideFromSide() }
                TransitionAnimation.Push -> anim { pushUp() }
                TransitionAnimation.Fade -> anim { fadeIn() }
            }
        }
    }

    override fun <T> currentDestinationResult(): LiveData<T?> {
        return controller.currentBackStackEntry!!
            .savedStateHandle
            .getLiveData(Args.RESULT)
    }

    override fun <T> currentDestinationResult(initialValue: T): LiveData<T> {
        return controller.currentBackStackEntry!!
            .savedStateHandle
            .getLiveData(Args.RESULT, initialValue)
    }

    override fun setDestinationResult(data: Any?) {
        controller.previousBackStackEntry
            ?.savedStateHandle
            ?.set(Args.RESULT, data)
    }
}
