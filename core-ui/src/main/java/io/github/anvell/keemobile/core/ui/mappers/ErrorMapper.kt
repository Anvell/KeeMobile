package io.github.anvell.keemobile.core.ui.mappers

import android.content.Context
import de.slackspace.openkeepass.exception.KeePassDatabaseUnreadableException
import io.github.anvell.keemobile.core.ui.R
import io.github.anvell.keemobile.domain.exceptions.DownloadsSaveException

object ErrorMapper {

    fun map(context: Context, error: Throwable): String? = when (error) {
        is KeePassDatabaseUnreadableException -> context.getString(R.string.error_wrong_master_key)
        is DownloadsSaveException -> context.getString(R.string.error_downloads_save_failed)
        else -> null
    }
}
