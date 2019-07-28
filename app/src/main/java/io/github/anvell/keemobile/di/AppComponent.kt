package io.github.anvell.keemobile.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import io.github.anvell.keemobile.presentation.home.HomeActivity
import io.github.anvell.keemobile.presentation.home.HomeViewModel
import io.github.anvell.keemobile.presentation.open.OpenFragment
import io.github.anvell.keemobile.presentation.open.OpenViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppAssistedModule::class,
    AppModule::class
])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }

    fun inject(activity: HomeActivity)
    fun inject(fragment: OpenFragment)
}
