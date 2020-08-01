package io.github.anvell.keemobile.domain.usecase

import dagger.Reusable
import io.github.anvell.keemobile.common.dispatchers.CoroutineDispatchers
import io.github.anvell.keemobile.domain.repository.RecentFilesRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Reusable
class ClearRecentFiles @Inject constructor(
    private val dispatchers: CoroutineDispatchers,
    private val recentFilesRepository: RecentFilesRepository
) {

    suspend operator fun invoke() = withContext(dispatchers.io) {
        recentFilesRepository.clearRecentFiles()
    }
}
