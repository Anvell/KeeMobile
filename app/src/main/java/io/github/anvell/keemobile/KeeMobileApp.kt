package io.github.anvell.keemobile

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class KeeMobileApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}