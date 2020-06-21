package io.github.anvell.keemobile.presentation.home

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(ActivityComponent::class)
interface HomeModule {
    @Binds
    fun viewModelFactory(factory: HomeViewModel_AssistedFactory): HomeViewModel.Factory
}
