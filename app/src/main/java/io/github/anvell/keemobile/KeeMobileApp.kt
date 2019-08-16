package io.github.anvell.keemobile

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import io.github.anvell.keemobile.di.DaggerAppComponent
import io.github.anvell.keemobile.di.InjectorProvider
import io.github.anvell.keemobile.presentation.ui.DayNightSwitcher
import timber.log.Timber

class KeeMobileApp : Application(), InjectorProvider {

    override val component by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        DayNightSwitcher.apply(DayNightSwitcher.NIGHT)
    }
}
