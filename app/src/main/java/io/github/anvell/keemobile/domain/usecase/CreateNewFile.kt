package io.github.anvell.keemobile.domain.usecase

import dagger.Reusable
import io.github.anvell.keemobile.domain.entity.FileSecrets
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.domain.repository.DatabaseRepository
import io.github.anvell.keemobile.common.rx.RxSchedulers
import io.reactivex.Single
import javax.inject.Inject

@Reusable
class CreateNewFile @Inject constructor(
    private val rxSchedulers: RxSchedulers,
    private val databaseRepository: DatabaseRepository
) {

    fun use(source: FileSource, secrets: FileSecrets) = Single.just(databaseRepository.createDatabase(source, secrets))
}
