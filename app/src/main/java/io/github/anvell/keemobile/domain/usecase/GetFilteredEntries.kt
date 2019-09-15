package io.github.anvell.keemobile.domain.usecase

import dagger.Reusable
import io.github.anvell.keemobile.common.extensions.scheduleOn
import io.github.anvell.keemobile.common.rx.RxSchedulers
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.SearchResults
import io.github.anvell.keemobile.domain.repository.DatabaseRepository
import io.reactivex.Single
import javax.inject.Inject

@Reusable
class GetFilteredEntries @Inject constructor(
    private val rxSchedulers: RxSchedulers,
    private val databaseRepository: DatabaseRepository
) {

    fun use(id: VaultId, filter: String) = Single.fromCallable {
        SearchResults(filter, databaseRepository.getFilteredEntries(id, filter))
    }
        .scheduleOn(rxSchedulers.computation())
}
