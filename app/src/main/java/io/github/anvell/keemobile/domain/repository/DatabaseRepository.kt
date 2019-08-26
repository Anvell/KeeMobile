package io.github.anvell.keemobile.domain.repository

import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.FileSecrets
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.domain.entity.OpenDatabase
import java.util.*

interface DatabaseRepository {

    fun getOpenDatabaseById(id: VaultId) : OpenDatabase

    fun readFromSource(source: FileSource, secrets: FileSecrets): VaultId

    fun createDatabase(source: FileSource, secrets: FileSecrets): VaultId
}
