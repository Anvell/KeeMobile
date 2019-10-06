package io.github.anvell.keemobile.domain.usecase

import dagger.Reusable
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.repository.DatabaseRepository
import io.reactivex.Single
import javax.inject.Inject

@Reusable
class CloseDatabase @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {

    fun use(id: VaultId) = Single.fromCallable { databaseRepository.closeDatabase(id) }
}
