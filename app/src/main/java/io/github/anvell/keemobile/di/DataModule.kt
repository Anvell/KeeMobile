package io.github.anvell.keemobile.di

import dagger.Module
import dagger.Provides
import io.github.anvell.keemobile.data.repository.AppSettingsRepositoryImpl
import io.github.anvell.keemobile.data.repository.DatabaseRepositoryImpl
import io.github.anvell.keemobile.data.repository.DownloadsRepositoryImpl
import io.github.anvell.keemobile.data.repository.RecentFilesRepositoryImpl
import io.github.anvell.keemobile.domain.repository.AppSettingsRepository
import io.github.anvell.keemobile.domain.repository.DatabaseRepository
import io.github.anvell.keemobile.domain.repository.DownloadsRepository
import io.github.anvell.keemobile.domain.repository.RecentFilesRepository

@Module
object DataModule {

    @Provides
    @JvmStatic
    fun provideDatabaseRepository(databaseRepository: DatabaseRepositoryImpl): DatabaseRepository = databaseRepository

    @Provides
    @JvmStatic
    fun provideRecentFilesRepository(recentFilesRepository: RecentFilesRepositoryImpl): RecentFilesRepository = recentFilesRepository

    @Provides
    @JvmStatic
    fun provideAppSettingsRepository(appSettingsRepository: AppSettingsRepositoryImpl): AppSettingsRepository = appSettingsRepository

    @Provides
    @JvmStatic
    fun provideDownloadsRepository(downloadsRepository: DownloadsRepositoryImpl): DownloadsRepository = downloadsRepository
}
