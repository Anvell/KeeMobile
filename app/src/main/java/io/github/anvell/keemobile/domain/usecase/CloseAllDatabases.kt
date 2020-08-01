package io.github.anvell.keemobile.domain.usecase

import dagger.Reusable
import io.github.anvell.keemobile.domain.repository.DatabaseRepository
import javax.inject.Inject

@Reusable
class CloseAllDatabases @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {

    operator fun invoke() {
        databaseRepository.closeAll()
    }
}
