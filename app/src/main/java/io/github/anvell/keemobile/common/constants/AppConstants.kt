package io.github.anvell.keemobile.common.constants

import io.github.anvell.keemobile.BuildConfig

object AppConstants {
    const val MAX_RECENT_FILES = 6

    const val FILE_RECENT_FILES = "recent_files.xml"
    const val FILE_PREFERENCES = "preferences.json"
    const val FILE_PROVIDER = BuildConfig.APPLICATION_ID + ".file.provider"
}
