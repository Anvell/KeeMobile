package io.github.anvell.keemobile.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.anvell.keemobile.data.repository.AppSettingsRepositoryImpl
import io.github.anvell.keemobile.data.repository.DatabaseRepositoryImpl
import io.github.anvell.keemobile.data.repository.DownloadsRepositoryImpl
import io.github.anvell.keemobile.data.repository.RecentFilesRepositoryImpl
import io.github.anvell.keemobile.domain.repository.AppSettingsRepository
import io.github.anvell.keemobile.domain.repository.DatabaseRepository
import io.github.anvell.keemobile.domain.repository.DownloadsRepository
import io.github.anvell.keemobile.domain.repository.RecentFilesRepository

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun provideDatabaseRepository(implementation: DatabaseRepositoryImpl): DatabaseRepository

    @Binds
    fun provideRecentFilesRepository(implementation: RecentFilesRepositoryImpl): RecentFilesRepository

    @Binds
    fun provideAppSettingsRepository(implementation: AppSettingsRepositoryImpl): AppSettingsRepository

    @Binds
    fun provideDownloadsRepository(implementation: DownloadsRepositoryImpl): DownloadsRepository
}
