package io.github.anvell.keemobile.presentation.open

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import io.github.anvell.keemobile.presentation.entry.EntryDetailsViewModel
import io.github.anvell.keemobile.presentation.entry.EntryDetailsViewModel_AssistedFactory
import io.github.anvell.keemobile.presentation.explore.ExploreViewModel
import io.github.anvell.keemobile.presentation.explore.ExploreViewModel_AssistedFactory
import io.github.anvell.keemobile.presentation.home.HomeViewModel
import io.github.anvell.keemobile.presentation.home.HomeViewModel_AssistedFactory

@Module
@InstallIn(FragmentComponent::class)
interface OpenModule {
    @Binds
    fun viewModelFactory(factory: OpenViewModel_AssistedFactory): OpenViewModel.Factory
}
