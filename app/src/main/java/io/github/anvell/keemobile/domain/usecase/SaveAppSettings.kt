package io.github.anvell.keemobile.domain.usecase

import dagger.Reusable
import io.github.anvell.keemobile.common.dispatchers.CoroutineDispatchers
import io.github.anvell.keemobile.domain.entity.AppSettings
import io.github.anvell.keemobile.domain.repository.AppSettingsRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Reusable
class SaveAppSettings @Inject constructor(
    private val dispatchers: CoroutineDispatchers,
    private val appSettingsRepository: AppSettingsRepository
) {

    suspend operator fun invoke(settings: AppSettings) = withContext(dispatchers.io) {
        appSettingsRepository.writeAppSettings(settings)
    }
}
