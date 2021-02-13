package io.github.anvell.keemobile.core.security

import androidx.biometric.BiometricManager

enum class BiometricServiceError {
    Unsupported,
    HardwareUnavailable,
    NoneEnrolled,
    NoHardware,
    SecurityUpdateRequired;

    companion object {
        fun from(errorCode: Int) = when (errorCode) {
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> Unsupported
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> NoHardware
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> HardwareUnavailable
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> NoneEnrolled
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> SecurityUpdateRequired
            else -> error("Unexpected error code: $errorCode")
        }
    }
}
