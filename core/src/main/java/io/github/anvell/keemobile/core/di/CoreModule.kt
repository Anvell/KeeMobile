package io.github.anvell.keemobile.core.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.anvell.keemobile.core.io.*
import io.github.anvell.keemobile.core.security.AndroidKeystoreEncryptionAes
import io.github.anvell.keemobile.core.security.KeystoreEncryption
import io.github.anvell.keemobile.domain.dispatchers.CoroutineDispatchers
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
interface CoreModule {
    @Binds
    fun provideStorageFile(implementation: StorageFileImpl): StorageFile

    @Binds
    fun provideInternalFile(implementation: InternalFileImpl): InternalFile

    @Binds
    fun provideMediaStoreFile(implementation: MediaStoreFileImpl): MediaStoreFile

    @Binds
    fun provideKeystoreEncryption(implementation: AndroidKeystoreEncryptionAes): KeystoreEncryption

    companion object {
        @Provides
        @Reusable
        fun provideCoroutineDispatchers() = CoroutineDispatchers(
            main = Dispatchers.Main,
            io = Dispatchers.IO,
            computation = Dispatchers.Default
        )
    }
}
