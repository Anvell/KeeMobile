package io.github.anvell.keemobile.domain.entity

import io.github.anvell.keemobile.common.extensions.toXmlTag
import io.github.anvell.keemobile.domain.alias.VaultId

sealed class FileSource(open val id: VaultId, open val name: String) {
    abstract fun toXml() : String

    class Storage(
        override val id: VaultId,
        override val name: String,
        val uri: String
    ) : FileSource(id, name) {

        override fun toXml() = (id.toXmlTag(ID) + name.toXmlTag(NAME) + uri.toXmlTag(URI)).toXmlTag(TAG)

        companion object Schema {
            const val TAG = "storage"
            const val ID = "id"
            const val NAME = "name"
            const val URI = "uri"
        }
    }

}
