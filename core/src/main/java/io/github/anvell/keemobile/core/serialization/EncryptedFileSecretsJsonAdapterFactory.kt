package io.github.anvell.keemobile.core.serialization

import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import io.github.anvell.keemobile.domain.entity.FileListEntrySecrets

object EncryptedFileSecretsJsonAdapterFactory {
    private const val TYPE_LABEL = "type"

    fun create(): PolymorphicJsonAdapterFactory<FileListEntrySecrets> {
        return PolymorphicJsonAdapterFactory.of(FileListEntrySecrets::class.java, TYPE_LABEL)
            .withSubtype(FileListEntrySecrets.Unspecified::class.java, "unspecified")
            .withSubtype(FileListEntrySecrets.Never::class.java, "rejected")
            .withSubtype(FileListEntrySecrets.Some::class.java, "some")
    }
}
