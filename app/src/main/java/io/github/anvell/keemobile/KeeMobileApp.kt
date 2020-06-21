package io.github.anvell.keemobile

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import io.github.anvell.keemobile.presentation.ui.DayNightSwitcher
import timber.log.Timber

@HiltAndroidApp
class KeeMobileApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        DayNightSwitcher.apply(DayNightSwitcher.NIGHT)
    }
}
