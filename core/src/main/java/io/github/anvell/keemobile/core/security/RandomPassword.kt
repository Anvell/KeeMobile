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
        const val LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz"
        const val UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        const val DIGITS = "0123456789"
        const val SYMBOLS = """!"#$%&'()*+,-./:;<=>?@[]^_{|}~"""

        fun all() = arrayOf(
            LOWERCASE_LETTERS,
            UPPERCASE_LETTERS,
            DIGITS,
            SYMBOLS
        )
    }
}
