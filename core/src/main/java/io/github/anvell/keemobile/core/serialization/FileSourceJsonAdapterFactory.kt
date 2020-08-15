package io.github.anvell.keemobile.core.serialization

import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import io.github.anvell.keemobile.domain.entity.FileSource

object FileSourceJsonAdapterFactory {
    private const val TYPE_LABEL = "type"
    private const val STORAGE = "storage"

    fun create(): PolymorphicJsonAdapterFactory<FileSource> {
        return PolymorphicJsonAdapterFactory.of(FileSource::class.java, TYPE_LABEL)
            .withSubtype(FileSource.Storage::class.java, STORAGE)
    }
}
