package io.github.anvell.keemobile.common.mapper

import android.content.Context
import dagger.Reusable
import de.slackspace.openkeepass.exception.KeePassDatabaseUnreadableException
import io.github.anvell.keemobile.R
import javax.inject.Inject

@Reusable
class ErrorMapper @Inject constructor(private val context: Context) {

    fun map(error: Throwable): String? {
        return when (error) {
            is KeePassDatabaseUnreadableException -> context.getString(R.string.error_wrong_master_key)
            else -> null
        }
    }
}
