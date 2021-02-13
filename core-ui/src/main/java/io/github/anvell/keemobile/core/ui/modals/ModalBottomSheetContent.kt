package io.github.anvell.keemobile.core.ui.modals

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.android.parcel.Parcelize

@Parcelize
class ModalBottomSheetContent(
    val destinationKey: String,
    @StringRes val headerId: Int,
    val rows: List<ModalBottomSheetRow>
) : Parcelable

@Parcelize
data class ModalBottomSheetRow(
    @StringRes val titleId: Int,
    val content: Parcelable
) : Parcelable
