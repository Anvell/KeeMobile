package io.github.anvell.keemobile.presentation.entrydetails

import io.github.anvell.async.Async
import io.github.anvell.async.Uninitialized
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.AppSettings
import io.github.anvell.keemobile.domain.entity.KeyEntry
import io.github.anvell.keemobile.domain.entity.OpenDatabase
import io.github.anvell.keemobile.presentation.data.EntryType
import io.github.anvell.keemobile.presentation.entrydetails.data.EntryDetailsArgs

data class EntryDetailsViewState(
    val activeDatabaseId: VaultId,
    val entryType: EntryType,
    val appSettings: Async<AppSettings> = Uninitialized,
    val activeDatabase: Async<OpenDatabase> = Uninitialized,
    val entry: Async<KeyEntry> = Uninitialized,
    val saveAttachmentQueue: Set<Int> = setOf(),
    val savedAttachments: Map<Int, String> = mapOf(),
    val attachmentStatus: Async<Unit> = Uninitialized
) {
    constructor(args: EntryDetailsArgs) : this(args.databaseId, args.entryType)
}
