package io.github.anvell.keemobile.domain.observers

import dagger.Reusable
import io.github.anvell.keemobile.domain.repository.DatabaseRepository
import javax.inject.Inject

@Reusable
class OpenDatabasesObserver @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {

    operator fun invoke() = databaseRepository.openDatabasesAsFlow
}
