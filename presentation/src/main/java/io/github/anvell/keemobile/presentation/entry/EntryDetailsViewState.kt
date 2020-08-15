package io.github.anvell.keemobile.presentation.entry

import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.datatypes.Async
import io.github.anvell.keemobile.domain.datatypes.Uninitialized
import io.github.anvell.keemobile.domain.entity.*
import java.util.*

data class EntryDetailsViewState(
    val activeDatabaseId: VaultId,
    val entryId: UUID,
    val historicEntryOf: UUID?,
    val activeDatabase: Async<OpenDatabase> = Uninitialized,
    val entry: Async<KeyEntry> = Uninitialized,
    val saveAttachmentQueue: List<Int> = listOf(),
    val savedAttachments: Map<Int, String> = mapOf(),
    val appSettings: Async<AppSettings> = Uninitialized,
    val errorSink: Throwable? = null
) {
    constructor(args: EntryDetailsArgs) : this(args.databaseId, args.entryId, args.historicEntryOf)
}
