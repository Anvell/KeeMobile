package io.github.anvell.keemobile.common.authentication

import io.github.anvell.keemobile.domain.exceptions.OneTimePasswordException
import org.apache.commons.codec.binary.Base32
import org.threeten.bp.Instant
import java.net.URI
import java.net.URISyntaxException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.floor
import kotlin.math.pow

class OneTimePassword(
    val secret: String,
    val digits: Int = 6,
    val period: Int = 30,
    val algorithm: Algorithm = Algorithm.SHA1,
    val baseTime: Instant = Instant.EPOCH
) {

    enum class Algorithm(val value: String) {
        SHA1("HmacSHA1"),
        SHA256("HmacSHA256"),
        SHA512("HmacSHA512")
    }

    fun calculate() = calculate(Instant.now())

    fun calculate(time: Instant) = calculate(timeToCounter(time))

    fun calculate(counter: Long): String {
        val key = Base32().decode(secret.toUpperCase(Locale.ENGLISH))
        val keySpec = SecretKeySpec(key, "RAW")

        try {
            val mac = Mac.getInstance(algorithm.value).apply {
                init(keySpec)
            }
            val hash = mac.doFinal(counterToByteArray(counter)).map(Byte::toInt)

            val offset = hash.last() and 0xf
            val code = ((hash[offset] and 0x7f) shl 24) or
                    ((hash[offset + 1] and 0xff) shl 16) or
                    ((hash[offset + 2] and 0xff) shl 8) or
                    (hash[offset + 3] and 0xff)

            return (code % 10.0.pow(digits)
                .toInt())
                .toString()
                .padStart(digits, '0')

        } catch (e: NoSuchAlgorithmException) {
            throw OneTimePasswordException(e.message, e.cause)
        } catch (e: InvalidKeyException) {
            throw OneTimePasswordException(e.message, e.cause)
        }
    }

    private fun counterToByteArray(counter: Long): ByteArray {
        val result = ByteArray(8)
        var value = counter

        for (i in result.size - 1 downTo 0) {
            result[i] = value.toByte()
            value = value ushr 8
        }

        return result
    }

    private fun timeToCounter(time: Instant): Long {
        return floor((time.epochSecond - baseTime.epochSecond).toDouble() / period).toLong()
    }

    companion object {

        private const val QUERY_SECRET = "secret"
        private const val QUERY_ISSUER = "issuer"
        private const val QUERY_ALGORITHM = "algorithm"
        private const val QUERY_DIGITS = "digits"
        private const val QUERY_PERIOD = "period"

        @Throws(URISyntaxException::class)
        fun from(sourceUri: String): OneTimePassword {
            val params = mutableMapOf<String, String>()
            val uri = URI.create(sourceUri)

            uri.query.split('&').forEach {
                val index = it.indexOf('=')

                if (index > 0 && index < it.length - 1) {
                    val param = it.slice(0 until index).toLowerCase(Locale.ENGLISH)
                    params[param] = it.drop(index + 1)
                } else {
                    throw URISyntaxException(it, "Query is not properly formatted.")
                }
            }

            return OneTimePassword(
                secret = params[QUERY_SECRET] ?: throw URISyntaxException(
                    sourceUri,
                    "A 'secret' parameter is required."
                ),
                digits = params[QUERY_DIGITS]?.toInt() ?: 6,
                period = params[QUERY_PERIOD]?.toInt() ?: 30,
                algorithm = params[QUERY_ALGORITHM]?.toAlgorithm() ?: Algorithm.SHA1
            )
        }
    }

}

private fun String.toAlgorithm() = when (this) {
    "SHA1" -> OneTimePassword.Algorithm.SHA1
    "SHA256" -> OneTimePassword.Algorithm.SHA256
    "SHA512" -> OneTimePassword.Algorithm.SHA512
    else -> throw OneTimePasswordException("No such algorithm: $this.")
}
