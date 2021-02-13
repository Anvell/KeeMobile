package io.github.anvell.keemobile.domain.entity

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

sealed class Secret : Parcelable {
    @JsonClass(generateAdapter = true)
    @Parcelize
    class Encrypted(
        val iv: ByteArray,
        val content: ByteArray
    ) : Secret()

    @JsonClass(generateAdapter = true)
    @Parcelize
    data class Unencrypted(val content: String) : Secret()
}
