package io.github.anvell.keemobile.domain.entity

import io.github.anvell.keemobile.domain.alias.VaultId

sealed class FileSource(open val id: VaultId, open val name: String) {

    class Storage(
            override val id: VaultId,
            override val name: String,
            val uri: String
    ) : FileSource(id, name)
}
