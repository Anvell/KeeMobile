package io.github.anvell.keemobile.data.repository

import dagger.Reusable
import io.github.anvell.keemobile.core.constants.AppConstants
import io.github.anvell.keemobile.core.extensions.readAsString
import io.github.anvell.keemobile.core.io.InternalFile
import io.github.anvell.keemobile.domain.entity.AppSettings
import io.github.anvell.keemobile.domain.repository.AppSettingsRepository
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject

@Reusable
class AppSettingsRepositoryImpl @Inject constructor(
    private val internalFile: InternalFile
) : AppSettingsRepository {

    override fun readAppSettings(): AppSettings {
        return internalFile.openInputStream(AppConstants.FILE_PREFERENCES)?.use { stream ->
            Json.decodeFromString<AppSettings>(stream.readAsString())
        } ?: throw IOException("Cannot open ${AppConstants.FILE_PREFERENCES}")
    }

    override fun writeAppSettings(settings: AppSettings): AppSettings {
        return internalFile.openOutputStream(AppConstants.FILE_PREFERENCES)?.use { stream ->
            settings.also {
                stream.write(Json.encodeToString(settings).toByteArray())
            }
        } ?: throw IOException("Cannot write ${AppConstants.FILE_PREFERENCES}")
    }
}
