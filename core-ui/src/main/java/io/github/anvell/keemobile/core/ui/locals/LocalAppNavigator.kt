package io.github.anvell.keemobile.core.ui.locals

import androidx.compose.runtime.compositionLocalOf
import io.github.anvell.keemobile.core.ui.navigation.AppNavigator

val LocalAppNavigator = compositionLocalOf<AppNavigator> {
    error("No AppNavigator provided.")
}
