package io.github.anvell.keemobile.core.security

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldNotContain
import org.junit.Test

private val UppercaseChars = Regex("[A-Z]+")
private val LowercaseChars = Regex("[a-z]+")
private val Numbers = Regex("[0-9]+")

class RandomPasswordTest {

    @Test
    fun `Random password is generated with specific length`() {
        RandomPassword.create(8) shouldHaveLength 8
    }

    @Test
    fun `Random password is generated with specific characters`() {
        RandomPassword.create(
            10,
            RandomPassword.Dictionaries.UppercaseLetters
        ) shouldContain UppercaseChars

        with(RandomPassword.create(10, RandomPassword.Dictionaries.LowercaseLetters)) {
            this shouldContain LowercaseChars
            this shouldNotContain UppercaseChars
        }

        RandomPassword.create(10, RandomPassword.Dictionaries.Digits) shouldContain Numbers

        RandomPassword.create(10, RandomPassword.Dictionaries.Symbols).forEach {
            RandomPassword.Dictionaries.Symbols.toList() shouldContain it
        }
    }
}
