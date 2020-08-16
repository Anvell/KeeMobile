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
import io.github.anvell.keemobile.core.security.AndroidKeystoreEncryptionAes
import io.github.anvell.keemobile.core.security.KeystoreEncryption
import io.github.anvell.keemobile.core.serialization.FileSecretsJsonAdapterFactory
import io.github.anvell.keemobile.core.serialization.FileSourceJsonAdapterFactory
import io.github.anvell.keemobile.domain.dispatchers.CoroutineDispatchers
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(ApplicationComponent::class)
interface CoreModule {
    @Binds
    fun provideRxSchedulers(implementation: RxSchedulersImpl): RxSchedulers

    @Binds
    fun provideStorageFile(implementation: StorageFileImpl): StorageFile

    @Binds
    fun provideInternalFile(implementation: InternalFileImpl): InternalFile

    @Binds
    fun provideMediaStoreFile(implementation: MediaStoreFileImpl): MediaStoreFile

    @Binds
    fun provideClipboardProvider(implementation: ClipboardProviderImpl): ClipboardProvider

    @Binds
    fun provideKeystoreEncryption(implementation: AndroidKeystoreEncryptionAes): KeystoreEncryption

    companion object {
        @Provides
        @Reusable
        fun provideMoshi(): Moshi {
            return Moshi.Builder()
                .add(FileSourceJsonAdapterFactory.create())
                .add(FileSecretsJsonAdapterFactory.create())
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
