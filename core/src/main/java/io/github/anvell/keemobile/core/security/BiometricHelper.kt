@file:Suppress("unused")

package io.github.anvell.keemobile.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import io.github.anvell.keemobile.core.constants.AppConstants.KeystoreAliasBiometric
import io.github.anvell.keemobile.domain.datatypes.Either
import io.github.anvell.keemobile.domain.datatypes.Left
import io.github.anvell.keemobile.domain.datatypes.Right
import io.github.anvell.keemobile.domain.datatypes.map
import io.github.anvell.keemobile.domain.entity.Secret
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.crypto.Cipher
import kotlin.coroutines.resume

class BiometricHelper(
    private val activity: FragmentActivity
) {
    private val biometricManager = BiometricManager.from(activity)

    fun canAuthenticate(): Either<BiometricServiceError, BiometricServiceStatus> {
        return when (val code = biometricManager.canAuthenticate(Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> Right(BiometricServiceStatus.Success)
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> Right(BiometricServiceStatus.Unknown)
            else -> Left(BiometricServiceError.from(code))
        }
    }

    suspend fun authenticateAndEncrypt(
        secret: Secret.Unencrypted,
        title: String,
        cancelLabel: String
    ): Either<BiometricAuthError, Secret.Encrypted?> {
        val cipher = CipherHelper.encrypt(createAes256CbcSpec())

        return authenticate(cipher, title, cancelLabel).map { cryptoObject ->
            cryptoObject?.cipher?.doFinal(secret.content.toByteArray(Charsets.UTF_8))?.let {
                Secret.Encrypted(cipher.iv, it)
            }
        }
    }

    suspend fun authenticateAndDecrypt(
        secret: Secret.Encrypted,
        title: String,
        cancelLabel: String
    ): Either<BiometricAuthError, Secret.Unencrypted?> {
        val cipher = CipherHelper.decrypt(createAes256CbcSpec(), secret.iv)

        return authenticate(cipher, title, cancelLabel).map { cryptoObject ->
            cryptoObject?.cipher?.doFinal(secret.content)?.toString(Charsets.UTF_8)?.let {
                Secret.Unencrypted(it)
            }
        }
    }

    private suspend fun authenticate(
        cipher: Cipher,
        title: String,
        cancelLabel: String
    ): Either<BiometricAuthError, BiometricPrompt.CryptoObject?> {
        return suspendCancellableCoroutine { coroutine ->
            val biometricPrompt = BiometricPrompt(activity, ContextCompat.getMainExecutor(activity),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        if (coroutine.isActive) {
                            coroutine.resume(Right(result.cryptoObject))
                        }
                    }

                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        if (coroutine.isActive) {
                            coroutine.resume(Left(BiometricAuthError.from(errorCode)))
                        }
                    }

                    override fun onAuthenticationFailed() {
                        /* This is invoked every time fingerprint was not recognised */
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setNegativeButtonText(cancelLabel)
                .setAllowedAuthenticators(Authenticators.BIOMETRIC_STRONG)
                .build()

            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    private fun createAes256CbcSpec() = KeyGenParameterSpec.Builder(
        KeystorePathUri + KeystoreAliasBiometric,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        .setRandomizedEncryptionRequired(true)
        .setUserAuthenticationRequired(true)
        .setKeySize(KeySize)
        .build()

    companion object {
        private const val KeySize = 256
        const val KeystorePathUri = "android-keystore://"
    }
}
