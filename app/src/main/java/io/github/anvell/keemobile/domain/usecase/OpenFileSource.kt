package io.github.anvell.keemobile.domain.usecase

import dagger.Reusable
import io.github.anvell.keemobile.common.dispatchers.CoroutineDispatchers
import io.github.anvell.keemobile.domain.entity.FileSecrets
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.domain.repository.DatabaseRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Reusable
class OpenFileSource @Inject constructor(
    private val dispatchers: CoroutineDispatchers,
    private val databaseRepository: DatabaseRepository
) {

    suspend operator fun invoke(source: FileSource, secrets: FileSecrets) =
        withContext(dispatchers.io) {
            databaseRepository.readFromSource(source, secrets)
        }
}
