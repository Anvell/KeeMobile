package io.github.anvell.keemobile.core.security

interface KeystoreEncryption {
    fun encrypt(
        keyAlias: String,
        data: ByteArray,
        associatedData: ByteArray
    ): ByteArray

    fun decrypt(
        keyAlias: String,
        data: ByteArray,
        associatedData: ByteArray
    ): ByteArray
}
