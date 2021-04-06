package io.github.anvell.keemobile.domain.observers

import dagger.Reusable
import io.github.anvell.keemobile.domain.repository.RecentFilesRepository
import javax.inject.Inject

@Reusable
class RecentFilesObserver @Inject constructor(
    private val recentFilesRepository: RecentFilesRepository
) {

    operator fun invoke() = recentFilesRepository.recentFilesAsFlow
}
