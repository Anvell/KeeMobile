package io.github.anvell.keemobile.domain.repository

import io.github.anvell.keemobile.domain.datatypes.Either
import io.github.anvell.keemobile.domain.entity.AppSettings

interface AppSettingsRepository {

    fun readAppSettings() : Either<Exception, AppSettings>

    fun writeAppSettings(settings: AppSettings) : Either<Exception, AppSettings>
}
