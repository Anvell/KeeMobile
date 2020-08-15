package io.github.anvell.keemobile.domain.repository

import io.github.anvell.keemobile.domain.entity.AppSettings

interface AppSettingsRepository {

    fun readAppSettings() : AppSettings

    fun writeAppSettings(settings: AppSettings) : AppSettings
}
