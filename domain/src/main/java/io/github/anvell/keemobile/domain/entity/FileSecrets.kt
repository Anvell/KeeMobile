package io.github.anvell.keemobile.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
sealed class FileSecrets : Parcelable

@Parcelize
@Serializable
data class KeyOnly(val masterKey: Secret) : FileSecrets()

@Parcelize
@Serializable
data class KeyFileOnly(val keyFile: FileSource) : FileSecrets()

@Parcelize
@Serializable
data class KeyWithKeyFile(val masterKey: Secret, val keyFile: FileSource) : FileSecrets()
