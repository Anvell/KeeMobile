package io.github.anvell.keemobile.core.ui.navigation

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.*

fun NavController.navigateToStartDestination() {
    navigateToStartDestination(optionsBuilder = {})
}

fun NavController.navigateToStartDestination(
    optionsBuilder: NavOptionsBuilder.() -> Unit,
) {
    val firstBackStackEntryId = getFirstBackStackEntry()?.destination?.id
    navigate(
        graph.findStartDestination().id,
        null,
        navOptions {
            optionsBuilder()
            firstBackStackEntryId?.let { popUpTo(it) { inclusive = true } }
        }
    )
}

fun NavController.navigate(
    uri: String,
    navOptions: NavOptions? = null,
) {
    navigate(Uri.parse(uri), navOptions)
}

fun NavController.navigate(
    uri: String,
    clearBackstack: Boolean,
) {
    val firstBackStackEntryId = getFirstBackStackEntry()?.destination?.id
    val options = if (clearBackstack && firstBackStackEntryId != null) {
        navOptions {
            popUpTo(firstBackStackEntryId) { inclusive = true }
        }
    } else {
        null
    }
    navigate(Uri.parse(uri), options)
}

fun NavController.navigate(
    uri: String,
    optionsBuilder: NavOptionsBuilder.() -> Unit,
) {
    navigate(Uri.parse(uri), navOptions(optionsBuilder))
}

fun NavController.navigate(
    uri: String,
    clearBackstack: Boolean,
    optionsBuilder: NavOptionsBuilder.() -> Unit,
) {
    val firstBackStackEntryId = getFirstBackStackEntry()?.destination?.id
    val options = if (clearBackstack && firstBackStackEntryId != null) {
        navOptions {
            optionsBuilder()
            popUpTo(firstBackStackEntryId) { inclusive = true }
        }
    } else {
        navOptions(optionsBuilder)
    }
    navigate(Uri.parse(uri), options)
}

fun NavController.navigate(
    @IdRes id: Int,
    args: Bundle? = null,
    optionsBuilder: NavOptionsBuilder.() -> Unit,
) {
    navigate(id, args, navOptions(optionsBuilder))
}

@SuppressLint("RestrictedApi")
fun NavController.getFirstBackStackEntry(): NavBackStackEntry? = backStack.firstOrNull()

/**
 * Finds the actual start destination of the graph, handling cases where the graph's starting
 * destination is itself a NavGraph.
 */
private fun NavGraph.findStartDestination(): NavDestination {
    var startDestination: NavDestination = this
    while (startDestination is NavGraph) {
        val parent = startDestination
        startDestination = parent.findNode(parent.startDestination)
            ?: return startDestination
    }
    return startDestination
}
