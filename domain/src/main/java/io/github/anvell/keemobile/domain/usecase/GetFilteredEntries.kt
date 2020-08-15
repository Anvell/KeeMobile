package io.github.anvell.keemobile.domain.usecase

import dagger.Reusable
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.dispatchers.CoroutineDispatchers
import io.github.anvell.keemobile.domain.entity.SearchResults
import io.github.anvell.keemobile.domain.repository.DatabaseRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Reusable
class GetFilteredEntries @Inject constructor(
    private val dispatchers: CoroutineDispatchers,
    private val databaseRepository: DatabaseRepository
) {

    suspend operator fun invoke(id: VaultId, filter: String) = withContext(dispatchers.io) {
        SearchResults(filter, databaseRepository.getFilteredEntries(id, filter))
    }
}
