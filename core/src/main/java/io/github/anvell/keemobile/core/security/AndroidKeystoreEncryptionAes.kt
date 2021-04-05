package io.github.anvell.keemobile.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.MasterKeys
import com.google.crypto.tink.integration.android.AndroidKeystoreAesGcm
import dagger.Reusable
import javax.inject.Inject

@Reusable
class AndroidKeystoreEncryptionAes @Inject constructor() : KeystoreEncryption {

    override fun encrypt(
        keyAlias: String,
        data: ByteArray,
        associatedData: ByteArray
    ): ByteArray {
        val keyId = MasterKeys.getOrCreate(createAes256GcmSpec(keyAlias))
        return AndroidKeystoreAesGcm(keyId).encrypt(data, associatedData)
    }

    override fun decrypt(
        keyAlias: String,
        data: ByteArray,
        associatedData: ByteArray
    ): ByteArray {
        val keyId = MasterKeys.getOrCreate(createAes256GcmSpec(keyAlias))
        return AndroidKeystoreAesGcm(keyId).decrypt(data, associatedData)
    }

    private fun createAes256GcmSpec(
        keyAlias: String
    ) = KeyGenParameterSpec.Builder(
        KeystorePathUri + keyAlias,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setKeySize(KeySize)
        .build()

    companion object {
        private const val KeySize = 256
        const val KeystorePathUri = "android-keystore://"
    }
}
