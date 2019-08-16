package io.github.anvell.keemobile.domain.entity

sealed class FileSource(open val id: String, open val name: String) {

    class Storage(
            override val id: String,
            override val name: String,
            val uri: String
    ) : FileSource(id, name)
}
