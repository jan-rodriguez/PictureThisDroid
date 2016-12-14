package com.jankrodriguez.picturethis

import android.app.Application
import com.facebook.stetho.Stetho

class PictureThisApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }
}

