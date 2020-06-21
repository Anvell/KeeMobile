package io.github.anvell.keemobile.presentation.explore

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import io.github.anvell.keemobile.presentation.home.HomeViewModel
import io.github.anvell.keemobile.presentation.home.HomeViewModel_AssistedFactory

@Module
@InstallIn(FragmentComponent::class)
interface ExploreModule {
    @Binds
    fun viewModelFactory(factory: ExploreViewModel_AssistedFactory): ExploreViewModel.Factory
}
