package io.github.anvell.keemobile.di

import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import io.github.anvell.keemobile.core.io.*
import io.github.anvell.keemobile.core.rx.RxSchedulers
import io.github.anvell.keemobile.core.rx.RxSchedulersImpl
import io.github.anvell.keemobile.core.serialization.FileSourceJsonAdapterFactory
import io.github.anvell.keemobile.domain.dispatchers.CoroutineDispatchers
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(ApplicationComponent::class)
interface CommonModule {
    @Binds
    fun provideRxSchedulers(rxSchedulers: RxSchedulersImpl): RxSchedulers

    @Binds
    fun provideStorageFile(storageFile: StorageFileImpl): StorageFile

    @Binds
    fun provideInternalFile(InternalFile: InternalFileImpl): InternalFile

    @Binds
    fun provideMediaStoreFile(mediaStoreFile: MediaStoreFileImpl): MediaStoreFile

    @Binds
    fun provideClipboardProvider(clipboardProvider: ClipboardProviderImpl): ClipboardProvider

    companion object {
        @Provides
        @Reusable
        fun provideMoshi(): Moshi {
            return Moshi.Builder()
                .add(FileSourceJsonAdapterFactory.create())
                .build()
        }

        @Provides
        @Reusable
        fun provideCoroutineDispatchers() = CoroutineDispatchers(
            main = Dispatchers.Main,
            io = Dispatchers.IO,
            computation = Dispatchers.Default
        )
    }
}
