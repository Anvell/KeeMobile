package io.github.anvell.keemobile.core.security

import java.security.SecureRandom

object RandomPassword {
    private val generator = SecureRandom()

    fun create(length: Int, vararg dictionaries: String = Dictionaries.all()): String {
        return (0 until length).fold("") { acc, _ ->
            val subset = dictionaries[generator.nextInt(dictionaries.size)]
            acc + subset[generator.nextInt(subset.length)]
        }
    }

    object Dictionaries {
        const val LowercaseLetters = "abcdefghijklmnopqrstuvwxyz"
        const val UppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        const val Digits = "0123456789"
        const val Symbols = """!"#$%&'()*+,-./:;<=>?@[]^_{|}~"""

        fun all() = arrayOf(
            LowercaseLetters,
            UppercaseLetters,
            Digits,
            Symbols
        )
    }
}
