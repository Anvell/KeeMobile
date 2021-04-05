package io.github.anvell.keemobile.core.security

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
        expectThat(RandomPassword.create(10, RandomPassword.Dictionaries.UppercaseLetters))
            .matches(Regex("[A-Z]+"))

        expectThat(RandomPassword.create(10, RandomPassword.Dictionaries.LowercaseLetters))
            .matches(Regex("[a-z]+"))

        expectThat(RandomPassword.create(10, RandomPassword.Dictionaries.Digits))
            .matches(Regex("[0-9]+"))

        RandomPassword.create(10, RandomPassword.Dictionaries.Symbols).forEach {
            expectThat(RandomPassword.Dictionaries.Symbols.toList())
                .any { isEqualTo(it) }
        }
    }
}
