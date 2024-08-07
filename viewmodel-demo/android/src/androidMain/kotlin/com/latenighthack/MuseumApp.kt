package com.latenighthack

import android.app.Application

class MuseumApp : Application() {
    override fun onCreate() {
        super.onCreate()

//        Core(
//            StoreDelegate(application, "main_db"),
//            KeyValueStoreDelegate(application.getSharedPreferences("", MODE_PRIVATE)),)
    }
}
