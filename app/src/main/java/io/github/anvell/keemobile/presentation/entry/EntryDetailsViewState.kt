package io.github.anvell.keemobile.presentation.entry

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.AppSettings
import io.github.anvell.keemobile.domain.entity.KeyEntry
import io.github.anvell.keemobile.domain.entity.OpenDatabase
import java.util.*

data class EntryDetailsViewState(
    val activeDatabaseId: VaultId,
    val entryId: UUID,
    val historicEntryOf: UUID?,
    val activeDatabase: Async<OpenDatabase> = Uninitialized,
    val entry: Async<KeyEntry> = Uninitialized,
    val saveAttachmentQueue: List<Int> = listOf(),
    val appSettings: Async<AppSettings> = Uninitialized
) : MvRxState {
    constructor(args: EntryDetailsArgs) : this(args.databaseId, args.entryId, args.historicEntryOf)
}
