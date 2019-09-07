package io.github.anvell.keemobile.presentation.explore

import android.os.Parcelable
import io.github.anvell.keemobile.domain.alias.VaultId
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ExploreArgs(
    val databaseId: VaultId
) : Parcelable
