package io.github.anvell.keemobile.domain.usecase

import dagger.Reusable
import io.github.anvell.keemobile.common.dispatchers.CoroutineDispatchers
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.repository.DatabaseRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Reusable
class GetOpenDatabase @Inject constructor(
    private val dispatchers: CoroutineDispatchers,
    private val databaseRepository: DatabaseRepository
) {

    suspend operator fun invoke(id: VaultId) = withContext(dispatchers.io) {
        databaseRepository.getOpenDatabaseById(id)
    }
}
