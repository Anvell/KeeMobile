package io.github.anvell.keemobile.common.extensions

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

fun Fragment.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(requireContext(), message, duration).apply { show() }
}

fun Fragment.snackbar(message: CharSequence, duration: Int = Snackbar.LENGTH_SHORT): Snackbar {
    return Snackbar.make(requireView(), message, duration).apply { show() }
}

fun Fragment.snackbar(
    message: CharSequence,
    actionLabel: CharSequence,
    duration: Int = Snackbar.LENGTH_LONG,
    action: () -> Unit,
    onShown: (() -> Unit)? = null,
    onDismissed: ((Int) -> Unit)? = null
): Snackbar {
    return Snackbar.make(requireView(), message, duration)
        .setAction(actionLabel) { action() }
        .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onShown(transientBottomBar: Snackbar?) {
                onShown?.invoke()
            }

            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                onDismissed?.invoke(event)
            }
        })
        .apply { show() }
}
