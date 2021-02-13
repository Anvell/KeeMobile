package io.github.anvell.keemobile.domain.entity

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

sealed class FileSecrets : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class KeyOnly(val masterKey: Secret) : FileSecrets()

@JsonClass(generateAdapter = true)
@Parcelize
data class KeyFileOnly(val keyFile: FileSource) : FileSecrets()

@JsonClass(generateAdapter = true)
@Parcelize
data class KeyWithKeyFile(val masterKey: Secret, val keyFile: FileSource) : FileSecrets()
