package io.github.anvell.keemobile.core.serialization

import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import io.github.anvell.keemobile.domain.entity.*

object FileSecretsJsonAdapterFactory {
    private const val TYPE_LABEL = "type"

    fun create(): PolymorphicJsonAdapterFactory<FileSecrets> {
        return PolymorphicJsonAdapterFactory.of(FileSecrets::class.java, TYPE_LABEL)
            .withSubtype(KeyOnly::class.java, "keyOnly")
            .withSubtype(KeyFileOnly::class.java, "keyFileOnly")
            .withSubtype(KeyWithKeyFile::class.java, "keyWithKeyFile")
    }
}
