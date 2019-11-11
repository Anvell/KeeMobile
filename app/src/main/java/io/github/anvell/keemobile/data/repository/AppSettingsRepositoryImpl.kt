package io.github.anvell.keemobile.data.repository

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Reusable
import io.github.anvell.keemobile.common.constants.AppConstants
import io.github.anvell.keemobile.common.extensions.readAsString
import io.github.anvell.keemobile.common.io.InternalFile
import io.github.anvell.keemobile.domain.entity.AppSettings
import io.github.anvell.keemobile.domain.entity.ViewMode
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
            val data = stream.readAsString()
            val result = moshi.adapter<AppSettings>(AppSettings::class.java).fromJson(data)

            if (result != null) {
                return result
            } else {
                throw IOException("Cannot parse ${AppConstants.FILE_PREFERENCES}")
            }
        }

        throw IOException("Cannot open ${AppConstants.FILE_PREFERENCES}")
    }

    override fun writeAppSettings(settings: AppSettings): AppSettings {
        val data = moshi.adapter<AppSettings>(AppSettings::class.java).toJson(settings)

        internalFile.openOutputStream(AppConstants.FILE_PREFERENCES)?.use { stream ->
            stream.write(data.toByteArray())
            return settings
        }

        throw IOException("Cannot write ${AppConstants.FILE_PREFERENCES}")
    }
}
