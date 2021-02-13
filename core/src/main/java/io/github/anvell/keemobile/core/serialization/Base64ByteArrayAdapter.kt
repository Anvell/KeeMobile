package io.github.anvell.keemobile.core.serialization

import com.google.crypto.tink.subtle.Base64
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class Base64ByteArrayAdapter : JsonAdapter<ByteArray>() {

    override fun fromJson(reader: JsonReader): ByteArray {
        return Base64.decode(reader.nextString())
            ?: error("Failed to decode Base64 string!")
    }

    override fun toJson(writer: JsonWriter, value: ByteArray?) {
        writer.value(Base64.encode(value))
    }
}
