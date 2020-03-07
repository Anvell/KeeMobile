package io.github.anvell.keemobile.common.extensions

import android.os.Bundle
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import java.io.Serializable

fun <T> Bundle.put(key: String, value: T) = when (value) {
    is Char -> putChar(key, value)
    is CharArray -> putCharArray(key, value)
    is CharSequence -> putCharSequence(key, value)
    is String -> putString(key, value)
    is Boolean -> putBoolean(key, value)
    is BooleanArray -> putBooleanArray(key, value)
    is Byte -> putByte(key, value)
    is ByteArray -> putByteArray(key, value)
    is Short -> putShort(key, value)
    is ShortArray -> putShortArray(key, value)
    is Int -> putInt(key, value)
    is IntArray -> putIntArray(key, value)
    is Long -> putLong(key, value)
    is LongArray -> putLongArray(key, value)
    is Float -> putFloat(key, value)
    is FloatArray -> putFloatArray(key, value)
    is Double -> putDouble(key, value)
    is DoubleArray -> putDoubleArray(key, value)
    is Size -> putSize(key, value)
    is SizeF -> putSizeF(key, value)
    is Bundle -> putBundle(key, value)
    is Parcelable -> putParcelable(key, value)
    is Serializable -> putSerializable(key, value)
    else -> throw IllegalArgumentException("Illegal type of $key.")
}
