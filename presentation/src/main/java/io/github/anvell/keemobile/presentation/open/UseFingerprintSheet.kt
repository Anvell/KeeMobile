package io.github.anvell.keemobile.presentation.open

import android.os.Parcelable
import androidx.fragment.app.FragmentManager
import io.github.anvell.keemobile.core.ui.modals.ModalBottomSheetContent
import io.github.anvell.keemobile.core.ui.modals.ModalBottomSheetRow
import io.github.anvell.keemobile.core.ui.modals.ModalBottomSheet
import io.github.anvell.keemobile.presentation.R
import kotlinx.parcelize.Parcelize

internal object UseFingerprintSheet {
    const val KEY = "KEY_USE_FINGERPRINT"

    fun show(manager: FragmentManager) {
        val bottomSheetContent = ModalBottomSheetContent(
            KEY,
            R.string.open_dialogs_use_fingerprint_prompt,
            listOf(
                ModalBottomSheetRow(
                    R.string.open_dialogs_use_fingerprint_ok,
                    Options.USE
                ),
                ModalBottomSheetRow(
                    R.string.open_dialogs_use_fingerprint_skip,
                    Options.SKIP
                ),
                ModalBottomSheetRow(
                    R.string.open_dialogs_use_fingerprint_never,
                    Options.NEVER
                )
            )
        )
        ModalBottomSheet().show(manager, bottomSheetContent)
    }

    @Parcelize
    enum class Options : Parcelable {
        USE, SKIP, NEVER
    }
}
