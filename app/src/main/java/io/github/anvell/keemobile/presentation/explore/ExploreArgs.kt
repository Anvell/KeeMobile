package io.github.anvell.keemobile.presentation.explore

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ExploreArgs(val databaseId: UUID) : Parcelable
