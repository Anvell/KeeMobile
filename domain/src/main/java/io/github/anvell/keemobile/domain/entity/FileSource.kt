package io.github.anvell.keemobile.domain.entity

import com.squareup.moshi.JsonClass
import io.github.anvell.keemobile.domain.alias.VaultId

sealed class FileSource(open val id: VaultId, open val name: String) {

    open val nameWithoutExtension: String
        get() = name.substringBeforeLast('.', name)

    @JsonClass(generateAdapter = true)
    class Storage(
        override val id: VaultId,
        override val name: String,
        val uri: String
    ) : FileSource(id, name)

}
