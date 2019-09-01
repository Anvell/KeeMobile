package io.github.anvell.keemobile.domain.usecase

import dagger.Reusable
import io.github.anvell.keemobile.common.extensions.scheduleOn
import io.github.anvell.keemobile.common.rx.RxSchedulers
import io.github.anvell.keemobile.domain.repository.RecentFilesRepository
import io.reactivex.Single
import javax.inject.Inject

@Reusable
class GetRecentFiles @Inject constructor(
    private val rxSchedulers: RxSchedulers,
    private val recentFilesRepository: RecentFilesRepository
) {

    fun use() = Single.fromCallable { recentFilesRepository.readRecentFiles() }
        .scheduleOn(rxSchedulers.io())
}
