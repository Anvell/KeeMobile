package io.github.anvell.keemobile.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import io.github.anvell.keemobile.presentation.drawer.DrawerFragment
import io.github.anvell.keemobile.presentation.explore.ExploreFragment
import io.github.anvell.keemobile.presentation.home.HomeActivity
import io.github.anvell.keemobile.presentation.open.OpenFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppAssistedModule::class,
    CommonModule::class,
    DataModule::class
])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }

    fun inject(activity: HomeActivity)
    fun inject(fragment: OpenFragment)
    fun inject(fragment: ExploreFragment)
    fun inject(fragment: DrawerFragment)
}
