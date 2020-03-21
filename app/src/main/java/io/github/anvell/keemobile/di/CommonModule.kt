package io.github.anvell.keemobile.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.github.anvell.keemobile.common.io.*
import io.github.anvell.keemobile.common.rx.RxSchedulers
import io.github.anvell.keemobile.common.rx.RxSchedulersImpl
import io.github.anvell.keemobile.data.serialization.FileSourceJsonAdapterFactory

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
    fun provideMediaStoreFile(mediaStoreFile: MediaStoreFileImpl): MediaStoreFile = mediaStoreFile

    @Provides
    @JvmStatic
    fun provideClipboardProvider(clipboardProvider: ClipboardProviderImpl): ClipboardProvider = clipboardProvider

    @Provides
    @JvmStatic
    @Reusable
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(FileSourceJsonAdapterFactory.create())
            .build()
    }
}
