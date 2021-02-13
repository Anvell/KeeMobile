package io.github.anvell.keemobile.core.serialization

import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import io.github.anvell.keemobile.domain.entity.Secret

object SecretJsonAdapterFactory {
    private const val TYPE_LABEL = "type"

    fun create(): PolymorphicJsonAdapterFactory<Secret> {
        return PolymorphicJsonAdapterFactory.of(Secret::class.java, TYPE_LABEL)
            .withSubtype(Secret.Unencrypted::class.java, "unencrypted")
            .withSubtype(Secret.Encrypted::class.java, "encrypted")
    }
}
