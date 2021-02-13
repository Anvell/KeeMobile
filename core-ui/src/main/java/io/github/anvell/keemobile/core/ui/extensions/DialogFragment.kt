package io.github.anvell.keemobile.core.ui.extensions

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

fun DialogFragment.show(manager: FragmentManager, args: Bundle, tag: String? = null) {
    arguments = args
    show(manager, tag)
}
