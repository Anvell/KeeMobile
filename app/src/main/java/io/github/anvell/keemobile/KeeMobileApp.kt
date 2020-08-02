package io.github.anvell.keemobile

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.github.anvell.keemobile.presentation.theme.DayNightSwitcher
import timber.log.Timber

@HiltAndroidApp
class KeeMobileApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        DayNightSwitcher.apply(DayNightSwitcher.NIGHT)
    }
}
