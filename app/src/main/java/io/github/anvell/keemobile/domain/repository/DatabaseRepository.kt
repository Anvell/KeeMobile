package io.github.anvell.keemobile.domain.repository

import io.github.anvell.keemobile.domain.entity.FileSecrets
import io.github.anvell.keemobile.domain.entity.FileSource
import java.util.*

interface DatabaseRepository {

    fun readFromSource(source: FileSource, secrets: FileSecrets): UUID

    fun createDatabase(source: FileSource, secrets: FileSecrets): UUID
}
