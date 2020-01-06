package io.github.anvell.keemobile.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.github.anvell.keemobile.common.io.*
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

    @Provides
    @JvmStatic
    fun provideInternalFile(InternalFile: InternalFileImpl): InternalFile = InternalFile

    @Provides
    @JvmStatic
    fun provideClipboardProvider(clipboardProvider: ClipboardProviderImpl): ClipboardProvider = clipboardProvider

    @Provides
    @JvmStatic
    @Reusable
    fun provideMoshi(): Moshi {
        return Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
}
