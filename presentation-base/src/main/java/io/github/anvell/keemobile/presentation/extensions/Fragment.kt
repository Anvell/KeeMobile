package io.github.anvell.keemobile.presentation.extensions

import android.app.Activity
import android.content.Intent
import android.view.inputmethod.InputMethodManager
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

fun Fragment.requestCreateFile(name: String, requestCode: Int, filter: String = "*/*") {
    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        putExtra(Intent.EXTRA_TITLE, name)
        flags = flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        type = filter
    }
    startActivityForResult(intent, requestCode)
}

fun Fragment.requestOpenFile(requestCode: Int, filter: String = "*/*") {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        flags = flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        type = filter
    }
    startActivityForResult(intent, requestCode)
}

fun Fragment.hideSoftKeyboard() {
    val manager = requireActivity().getSystemService(
        Activity.INPUT_METHOD_SERVICE
    ) as InputMethodManager?
    manager?.hideSoftInputFromWindow(view?.windowToken, 0)
}
