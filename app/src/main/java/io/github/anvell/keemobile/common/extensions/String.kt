package io.github.anvell.keemobile.common.extensions

import java.lang.StringBuilder
import java.security.MessageDigest

private const val HEX = "0123456789ABCDEF"
private const val SHA_256 = "SHA-256"
private const val SHA_512 = "SHA-512"

fun String.toSha256() = hash(SHA_256)

fun String.toSha512() = hash(SHA_512)

private fun String.hash(type: String): String {

    val bytes = MessageDigest.getInstance(type)
            .digest(toByteArray())
    val result = StringBuilder(bytes.size * 2)

    for (byte in bytes) {
        val n = byte.toInt()
        result.append(HEX[n shr 4 and 0x0f])
        result.append(HEX[n and 0x0f])
    }

    return result.toString()
}

fun String.toXmlTag(tag: String) = "<$tag>$this</$tag>"
