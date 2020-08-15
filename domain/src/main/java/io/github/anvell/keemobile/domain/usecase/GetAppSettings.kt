package io.github.anvell.keemobile.domain.usecase

import dagger.Reusable
import io.github.anvell.keemobile.domain.dispatchers.CoroutineDispatchers
import io.github.anvell.keemobile.domain.repository.AppSettingsRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Reusable
class GetAppSettings @Inject constructor(
    private val dispatchers: CoroutineDispatchers,
    private val appSettingsRepository: AppSettingsRepository
) {

    suspend operator fun invoke() = withContext(dispatchers.io) {
        appSettingsRepository.readAppSettings()
    }
}
