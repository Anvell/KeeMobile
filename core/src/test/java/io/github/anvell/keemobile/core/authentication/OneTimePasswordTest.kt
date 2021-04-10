package io.github.anvell.keemobile.core.authentication

import io.kotest.matchers.shouldBe
import org.junit.Test
import java.time.Instant

private const val Secret = "LMKBFLV6XVLH5BRISVA5U2NHI4MWUFD5"

class OneTimePasswordTest {

    @Test
    fun `TOTP is properly generated`() {
        val instant = Instant.parse("2049-05-15T09:00:00.000Z")

        OneTimePassword(Secret).calculate(instant) shouldBe "798457"
    }

    @Test
    fun `OTP Uri is properly parsed`() {
        with(
            OneTimePassword.from(
                "otpauth://totp/N?secret=$Secret&issuer=None&algorithm=SHA256&digits=8&period=60"
            )
        ) {
            secret shouldBe Secret
            algorithm shouldBe OneTimePassword.Algorithm.SHA256
            digits shouldBe 8
            period shouldBe 60
        }
    }
}
