package io.github.anvell.keemobile.di

import dagger.Module
import dagger.Provides
import io.github.anvell.keemobile.common.io.StorageFileImpl
import io.github.anvell.keemobile.data.repository.DatabaseRepositoryImpl
import io.github.anvell.keemobile.common.rx.RxSchedulersImpl
import io.github.anvell.keemobile.domain.repository.DatabaseRepository
import io.github.anvell.keemobile.common.rx.RxSchedulers
import io.github.anvell.keemobile.common.io.StorageFile

@Module
object DataModule {

    @Provides
    @JvmStatic
    fun provideDatabaseRepository(databaseRepository: DatabaseRepositoryImpl): DatabaseRepository = databaseRepository
}
