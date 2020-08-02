package io.github.anvell.keemobile.core.io

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.getSystemService
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@Reusable
class ClipboardProviderImpl @Inject constructor(@ApplicationContext private val context: Context) :
    ClipboardProvider {

    override fun putText(label: String, content: String): Boolean {
        val clipboard = context.getSystemService<ClipboardManager>()

        return if (clipboard != null) {
            try {
                clipboard.setPrimaryClip(ClipData.newPlainText(label, content))
                true
            } catch (e: RuntimeException) {
                false
            }

        } else {
            false
        }
    }
}
