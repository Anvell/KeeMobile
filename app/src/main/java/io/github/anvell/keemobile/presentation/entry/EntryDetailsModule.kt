package io.github.anvell.keemobile.presentation.entry

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import io.github.anvell.keemobile.presentation.explore.ExploreViewModel
import io.github.anvell.keemobile.presentation.explore.ExploreViewModel_AssistedFactory
import io.github.anvell.keemobile.presentation.home.HomeViewModel
import io.github.anvell.keemobile.presentation.home.HomeViewModel_AssistedFactory

@Module
@InstallIn(FragmentComponent::class)
interface EntryDetailsModule {
    @Binds
    fun viewModelFactory(factory: EntryDetailsViewModel_AssistedFactory): EntryDetailsViewModel.Factory
}
