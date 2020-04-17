package io.github.anvell.keemobile.common.security

import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.*

class RandomPasswordTest {

    @Test
    fun `Random password is generated with specific length`() {
        expectThat(RandomPassword.create(8))
            .hasLength(8)
    }

    @Test
    fun `Random password is generated with specific characters`() {
        expectThat(RandomPassword.create(10, RandomPassword.Dictionaries.UPPERCASE_LETTERS))
            .matches(Regex("[A-Z]+"))

        expectThat(RandomPassword.create(10, RandomPassword.Dictionaries.LOWERCASE_LETTERS))
            .matches(Regex("[a-z]+"))

        expectThat(RandomPassword.create(10, RandomPassword.Dictionaries.DIGITS))
            .matches(Regex("[0-9]+"))

        RandomPassword.create(10, RandomPassword.Dictionaries.SYMBOLS).forEach {
            expectThat(RandomPassword.Dictionaries.SYMBOLS.toList())
                .any { isEqualTo(it) }
        }
    }
}
