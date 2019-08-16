package io.github.anvell.keemobile.di

import dagger.Module
import dagger.Provides
import io.github.anvell.keemobile.common.io.StorageFile
import io.github.anvell.keemobile.common.io.StorageFileImpl
import io.github.anvell.keemobile.common.rx.RxSchedulers
import io.github.anvell.keemobile.common.rx.RxSchedulersImpl

@Module
object CommonModule {

    @Provides
    @JvmStatic
    fun provideRxSchedulers(rxSchedulers: RxSchedulersImpl): RxSchedulers = rxSchedulers

    @Provides
    @JvmStatic
    fun provideStorageFile(storageFile: StorageFileImpl): StorageFile = storageFile
}
