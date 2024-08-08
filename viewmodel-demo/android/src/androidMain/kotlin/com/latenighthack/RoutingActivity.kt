package com.latenighthack

import android.app.Activity
import android.os.Bundle
import com.latenighthack.viewmodel.ActivitiesProvider
import com.latenighthack.viewmodel.startup

class RoutingActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val core = (application as MainApplication).core

        startup(core, MainAndroidNavigator(this, application as ActivitiesProvider)) {
            runOnUiThread {
                finish()
            }
        }
//
//        GlobalScope.launch(Dispatchers.Main.immediate) {
//            Startup.start(core, navigator) {
//                runOnUiThread {
//                    finish()
//                }
//            }
//        }
    }
}
