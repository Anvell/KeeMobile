package io.github.anvell.keemobile.domain.usecase

import dagger.Reusable
import io.github.anvell.keemobile.common.extensions.scheduleOn
import io.github.anvell.keemobile.common.rx.RxSchedulers
import io.github.anvell.keemobile.domain.repository.AppSettingsRepository
import io.reactivex.Single
import javax.inject.Inject

@Reusable
class GetAppSettings @Inject constructor(
    private val rxSchedulers: RxSchedulers,
    private val appSettingsRepository: AppSettingsRepository
) {

    fun use() = Single.fromCallable { appSettingsRepository.readAppSettings() }
        .scheduleOn(rxSchedulers.io())
}
