package com.latenighthack

import android.app.Activity
import com.latenighthack.activities.HomeActivity
import com.latenighthack.viewmodel.ActivitiesProvider
import com.latenighthack.viewmodel.AndroidNavigator
import com.latenighthack.viewmodel.core.IHomeViewModel
import com.latenighthack.viewmodel.core.Navigator

class MainAndroidNavigator(activity: Activity, activitiesProvider: ActivitiesProvider)
    : AndroidNavigator(activity, activitiesProvider), Navigator {
    override fun close() {
        activity.finish()
    }

    override fun navigateTo(home: IHomeViewModel.Args) = pushActivityAsRoot(home, HomeActivity::class)
}
