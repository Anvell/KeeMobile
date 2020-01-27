package io.github.anvell.keemobile.presentation.entry

import android.os.Parcelable
import io.github.anvell.keemobile.domain.alias.VaultId
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class EntryDetailsArgs(
    val databaseId: VaultId,
    val entryId: UUID,
    val historicEntryOf: UUID? = null
) : Parcelable
