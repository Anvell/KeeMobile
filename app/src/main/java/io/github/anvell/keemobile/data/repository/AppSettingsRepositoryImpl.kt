package io.github.anvell.keemobile.data.repository

import com.squareup.moshi.Moshi
import dagger.Reusable
import io.github.anvell.keemobile.common.constants.AppConstants
import io.github.anvell.keemobile.common.extensions.readAsString
import io.github.anvell.keemobile.common.io.InternalFile
import io.github.anvell.keemobile.domain.entity.AppSettings
import io.github.anvell.keemobile.domain.repository.AppSettingsRepository
import java.io.IOException
import javax.inject.Inject

@Reusable
class AppSettingsRepositoryImpl @Inject constructor(
    private val internalFile: InternalFile,
    private val moshi: Moshi
) : AppSettingsRepository {

    override fun readAppSettings(): AppSettings {
        internalFile.openInputStream(AppConstants.FILE_PREFERENCES)?.use { stream ->
            val data = moshi.adapter(AppSettings::class.java)
                .fromJson(stream.readAsString())
            return data ?: throw IOException("Cannot parse ${AppConstants.FILE_PREFERENCES}")
        }

        throw IOException("Cannot open ${AppConstants.FILE_PREFERENCES}")
    }

    override fun writeAppSettings(settings: AppSettings): AppSettings {
        internalFile.openOutputStream(AppConstants.FILE_PREFERENCES)?.use { stream ->
            val data = moshi.adapter(AppSettings::class.java).toJson(settings)
            stream.write(data.toByteArray())
            return settings
        }

        throw IOException("Cannot write ${AppConstants.FILE_PREFERENCES}")
    }
}
