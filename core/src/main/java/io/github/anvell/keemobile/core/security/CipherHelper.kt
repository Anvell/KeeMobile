package io.github.anvell.keemobile.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec

internal object CipherHelper {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"

    fun encrypt(
        keyGenParameterSpec: KeyGenParameterSpec
    ): Cipher = Cipher.getInstance(
        keyGenParameterSpec.toTransformationString()
    ).apply {
        init(Cipher.ENCRYPT_MODE, getOrCreate(keyGenParameterSpec))
    }

    fun decrypt(
        keyGenParameterSpec: KeyGenParameterSpec,
        iv: ByteArray
    ): Cipher = Cipher.getInstance(
        keyGenParameterSpec.toTransformationString()
    ).apply {
        init(Cipher.DECRYPT_MODE, getOrCreate(keyGenParameterSpec), IvParameterSpec(iv))
    }

    private fun getOrCreate(keyGenParameterSpec: KeyGenParameterSpec): Key {
        if (!keyExists(keyGenParameterSpec.keystoreAlias)) {
            generateKey(keyGenParameterSpec)
        }

        return KeyStore.getInstance(ANDROID_KEYSTORE).run {
            load(null)
            getKey(keyGenParameterSpec.keystoreAlias, null)
        }
    }

    private fun generateKey(keyGenParameterSpec: KeyGenParameterSpec) {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    private fun keyExists(keyAlias: String): Boolean {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        return keyStore.containsAlias(keyAlias)
    }

    private fun KeyGenParameterSpec.toTransformationString(): String {
        return "${KeyProperties.KEY_ALGORITHM_AES}/${blockModes.first()}/${encryptionPaddings.first()}"
    }
}
