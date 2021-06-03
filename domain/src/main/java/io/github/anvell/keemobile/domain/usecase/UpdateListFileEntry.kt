package io.github.anvell.keemobile.domain.usecase

import dagger.Reusable
import io.github.anvell.either.flatMap
import io.github.anvell.either.map
import io.github.anvell.keemobile.domain.dispatchers.CoroutineDispatchers
import io.github.anvell.keemobile.domain.entity.FileListEntry
import io.github.anvell.keemobile.domain.repository.RecentFilesRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Reusable
class UpdateListFileEntry @Inject constructor(
    private val dispatchers: CoroutineDispatchers,
    private val recentFilesRepository: RecentFilesRepository
) {

    suspend operator fun invoke(entry: FileListEntry) = withContext(dispatchers.io) {
        recentFilesRepository.readRecentFiles().map { recentFiles ->
            recentFiles.filter {
                it.vault.id != entry.vault.id
            }.plus(entry)
        }.flatMap {
            recentFilesRepository.writeRecentFiles(it)
        }
    }
}
