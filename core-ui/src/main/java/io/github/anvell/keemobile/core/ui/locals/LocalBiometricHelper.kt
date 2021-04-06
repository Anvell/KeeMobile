package io.github.anvell.keemobile.core.ui.locals

import androidx.compose.runtime.compositionLocalOf
import io.github.anvell.keemobile.core.security.BiometricHelper

val LocalBiometricHelper = compositionLocalOf<BiometricHelper> {
    error("No BiometricHelper provided.")
}
