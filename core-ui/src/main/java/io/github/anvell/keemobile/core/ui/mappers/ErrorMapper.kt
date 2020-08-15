package io.github.anvell.keemobile.core.ui.mappers

import android.content.Context
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import de.slackspace.openkeepass.exception.KeePassDatabaseUnreadableException
import io.github.anvell.keemobile.domain.exceptions.DownloadsSaveException
import io.github.anvell.keemobile.core.ui.R
import javax.inject.Inject

@Reusable
class ErrorMapper @Inject constructor(@ApplicationContext private val context: Context) {

    fun map(error: Throwable, block: (String) -> Unit) {
        when (error) {
            is KeePassDatabaseUnreadableException -> context.getString(R.string.error_wrong_master_key)
            is DownloadsSaveException -> context.getString(R.string.error_downloads_save_failed)
            else -> null
        }?.let {
            block(it)
        }
    }
}
