package io.github.anvell.keemobile.core.extensions

import android.webkit.MimeTypeMap
import java.lang.StringBuilder
import java.security.MessageDigest

private const val Hex = "0123456789ABCDEF"
private const val Sha256 = "SHA-256"
private const val Sha512 = "SHA-512"

fun String.toSha256() = hash(Sha256)

fun String.toSha512() = hash(Sha512)

private fun String.hash(type: String): String {

    val bytes = MessageDigest.getInstance(type)
        .digest(toByteArray())
    val result = StringBuilder(bytes.size * 2)

    for (byte in bytes) {
        val n = byte.toInt()
        result.append(Hex[n shr 4 and 0x0f])
        result.append(Hex[n and 0x0f])
    }

    return result.toString()
}

fun String.toXmlTag(tag: String) = "<$tag>$this</$tag>"

fun String.getMimeTypeFromFileName(): String {
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(
        substringAfterLast('.', "")
    ) ?: "*/*"
}
