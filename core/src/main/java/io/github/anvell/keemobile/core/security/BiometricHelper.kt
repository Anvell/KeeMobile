package io.github.anvell.keemobile.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.anvell.keemobile.core.constants.AppConstants.KEYSTORE_ALIAS_BIOMETRIC
import io.github.anvell.keemobile.domain.datatypes.Either
import io.github.anvell.keemobile.domain.datatypes.Left
import io.github.anvell.keemobile.domain.datatypes.Right
import io.github.anvell.keemobile.domain.datatypes.map
import io.github.anvell.keemobile.domain.entity.Secret
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.crypto.Cipher
import javax.inject.Inject
import kotlin.coroutines.resume

@Reusable
class BiometricHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val biometricManager = BiometricManager.from(context)

    fun canAuthenticate(): Either<BiometricServiceError, BiometricServiceStatus> {
        return when (val code = biometricManager.canAuthenticate(Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> Right(BiometricServiceStatus.Success)
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> Right(BiometricServiceStatus.Unknown)
            else -> Left(BiometricServiceError.from(code))
        }
    }

    suspend fun authenticateAndEncrypt(
        fragment: Fragment,
        secret: Secret.Unencrypted,
        title: String,
        cancelLabel: String
    ): Either<BiometricAuthError, Secret.Encrypted?> {
        val cipher = CipherHelper.encrypt(createAes256CbcSpec())

        return authenticate(fragment, cipher, title, cancelLabel).map { cryptoObject ->
            cryptoObject?.cipher?.doFinal(secret.content.toByteArray(Charsets.UTF_8))?.let {
                Secret.Encrypted(cipher.iv, it)
            }
        }
    }

    suspend fun authenticateAndDecrypt(
        fragment: Fragment,
        secret: Secret.Encrypted,
        title: String,
        cancelLabel: String
    ): Either<BiometricAuthError, Secret.Unencrypted?> {
        val cipher = CipherHelper.decrypt(createAes256CbcSpec(), secret.iv)

        return authenticate(fragment, cipher, title, cancelLabel).map { cryptoObject ->
            cryptoObject?.cipher?.doFinal(secret.content)?.toString(Charsets.UTF_8)?.let {
                Secret.Unencrypted(it)
            }
        }
    }

    private suspend fun authenticate(
        fragment: Fragment,
        cipher: Cipher,
        title: String,
        cancelLabel: String
    ) = suspendCancellableCoroutine<Either<BiometricAuthError, BiometricPrompt.CryptoObject?>> { coroutine ->
        val biometricPrompt = BiometricPrompt(fragment, ContextCompat.getMainExecutor(context),
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

    private fun createAes256CbcSpec() = KeyGenParameterSpec.Builder(
        KEYSTORE_PATH_URI + KEYSTORE_ALIAS_BIOMETRIC,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        .setRandomizedEncryptionRequired(true)
        .setUserAuthenticationRequired(true)
        .setKeySize(KEY_SIZE)
        .build()

    companion object {
        private const val KEY_SIZE = 256
        const val KEYSTORE_PATH_URI = "android-keystore://"
    }
}
