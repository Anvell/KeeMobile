package io.github.anvell.keemobile.core.security

import androidx.biometric.BiometricPrompt

sealed class BiometricAuthError : Exception() {
    object HardwareUnavailable : BiometricAuthError()
    object UnableToProcess : BiometricAuthError()
    object Timeout : BiometricAuthError()
    object NoSpace : BiometricAuthError()
    object Canceled : BiometricAuthError()
    object Lockout : BiometricAuthError()
    object Vendor : BiometricAuthError()
    object LockoutPermanent : BiometricAuthError()
    object UserCanceled : BiometricAuthError()
    object NoBiometrics : BiometricAuthError()
    object HardwareNotPresent : BiometricAuthError()
    object NegativeButton : BiometricAuthError()
    object NoDeviceCredential : BiometricAuthError()

    companion object {
        fun from(errorCode: Int) = when (errorCode) {
            BiometricPrompt.ERROR_HW_UNAVAILABLE -> HardwareUnavailable
            BiometricPrompt.ERROR_UNABLE_TO_PROCESS -> UnableToProcess
            BiometricPrompt.ERROR_TIMEOUT -> Timeout
            BiometricPrompt.ERROR_NO_SPACE -> NoSpace
            BiometricPrompt.ERROR_CANCELED -> Canceled
            BiometricPrompt.ERROR_LOCKOUT -> Lockout
            BiometricPrompt.ERROR_VENDOR -> Vendor
            BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> LockoutPermanent
            BiometricPrompt.ERROR_USER_CANCELED -> UserCanceled
            BiometricPrompt.ERROR_NO_BIOMETRICS -> NoBiometrics
            BiometricPrompt.ERROR_HW_NOT_PRESENT -> HardwareNotPresent
            BiometricPrompt.ERROR_NEGATIVE_BUTTON -> NegativeButton
            BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> NoDeviceCredential
            else -> error("Unexpected error code: $errorCode")
        }
    }
}
