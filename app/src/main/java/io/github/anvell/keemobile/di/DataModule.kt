package io.github.anvell.keemobile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import io.github.anvell.keemobile.data.repository.AppSettingsRepositoryImpl
import io.github.anvell.keemobile.data.repository.DatabaseRepositoryImpl
import io.github.anvell.keemobile.data.repository.DownloadsRepositoryImpl
import io.github.anvell.keemobile.data.repository.RecentFilesRepositoryImpl
import io.github.anvell.keemobile.domain.repository.AppSettingsRepository
import io.github.anvell.keemobile.domain.repository.DatabaseRepository
import io.github.anvell.keemobile.domain.repository.DownloadsRepository
import io.github.anvell.keemobile.domain.repository.RecentFilesRepository

@Module
@InstallIn(ApplicationComponent::class)
interface DataModule {
    @Binds
    fun provideDatabaseRepository(databaseRepository: DatabaseRepositoryImpl): DatabaseRepository

    @Binds
    fun provideRecentFilesRepository(recentFilesRepository: RecentFilesRepositoryImpl): RecentFilesRepository

    @Binds
    fun provideAppSettingsRepository(appSettingsRepository: AppSettingsRepositoryImpl): AppSettingsRepository

    @Binds
    fun provideDownloadsRepository(downloadsRepository: DownloadsRepositoryImpl): DownloadsRepository
}
