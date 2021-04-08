package io.github.anvell.keemobile.domain.usecase

import dagger.Reusable
import io.github.anvell.either.Either
import io.github.anvell.either.Left
import io.github.anvell.either.Right
import io.github.anvell.either.flatMap
import io.github.anvell.keemobile.domain.dispatchers.CoroutineDispatchers
import io.github.anvell.keemobile.domain.entity.KeyEntry
import io.github.anvell.keemobile.domain.entity.VaultId
import io.github.anvell.keemobile.domain.exceptions.EntryNotFoundException
import io.github.anvell.keemobile.domain.repository.DatabaseRepository
import java.util.*
import javax.inject.Inject

@Reusable
@Suppress("RedundantSuspendModifier")
class GetDatabaseEntry @Inject constructor(
    private val dispatchers: CoroutineDispatchers,
    private val databaseRepository: DatabaseRepository
) {

    suspend operator fun invoke(
        databaseId: VaultId,
        entryId: UUID
    ): Either<Exception, KeyEntry> {
        return databaseRepository.getOpenDatabaseById(databaseId).flatMap { openDatabase ->
            openDatabase.database
                .findEntry { it.uuid == entryId }
                ?.second
                ?.let { Right(it) }
                ?: Left(EntryNotFoundException())
        }
    }
}
