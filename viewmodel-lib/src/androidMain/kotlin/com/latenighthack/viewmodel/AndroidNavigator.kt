package com.latenighthack.viewmodel

import android.app.Activity
import android.content.Intent
import kotlin.reflect.KClass

public interface ActivitiesProvider {
    public fun allActivities(): List<Activity>

    public fun addActivity(activity: Activity)

    public fun removeActivity(activity: Activity)
}

public abstract class AndroidNavigator(
    protected val activity: Activity,
    protected val activitiesProvider: ActivitiesProvider
) {
    protected fun <T : Activity, U : Any> intentFor(args: U, klass: KClass<T>): Intent {
        val argsBundle = (args as NavigatorArgs).bundle

        return Intent(activity, klass.java).apply {
            putExtra("args", argsBundle)
        }
    }

    protected fun <T : Activity, U : Any> pushActivity(args: U, activityClass: KClass<T>) {
        activity.startActivity(intentFor(args, activityClass))
    }

    protected fun <T : Activity, U : Any> pushActivityAsRoot(args: U, activityClass: KClass<T>) {
        val currentActivities = activitiesProvider.allActivities()

        activity.startActivity(intentFor(args, activityClass))

        currentActivities.forEach { it.finish() }
    }

    protected fun <T : Activity, U : Any> pushActivityOverRoot(args: U, activityClass: KClass<T>) {
        val currentActivities = activitiesProvider.allActivities()

        activity.startActivity(intentFor(args, activityClass))

        currentActivities.forEachIndexed { i, activity ->
            if (i == 0) return@forEachIndexed

            activity.finish()
        }
    }
}
