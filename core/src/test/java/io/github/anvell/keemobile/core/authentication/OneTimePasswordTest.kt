package io.github.anvell.keemobile.core.authentication

import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant

class OneTimePasswordTest {

    val SECRET = "LMKBFLV6XVLH5BRISVA5U2NHI4MWUFD5"

    @Test
    fun `TOTP is properly generated`() {
        val instant = Instant.parse("2049-05-15T09:00:00.000Z")

        expectThat(OneTimePassword(SECRET).calculate(instant))
            .isEqualTo("798457")
    }

    @Test
    fun `OTP Uri is properly parsed`() {
        val otp = OneTimePassword.from(
            "otpauth://totp/N?secret=$SECRET&issuer=None&algorithm=SHA256&digits=8&period=60"
        )

        expectThat(otp.secret).isEqualTo(SECRET)
        expectThat(otp.algorithm).isEqualTo(OneTimePassword.Algorithm.SHA256)
        expectThat(otp.digits).isEqualTo(8)
        expectThat(otp.period).isEqualTo(60)
    }
}
