package com.latenighthack

import android.app.Activity
import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri
import com.latenighthack.ktstore.getAppContext
import com.latenighthack.viewmodel.ActivitiesProvider
import com.latenighthack.viewmodel.core.Core
import java.lang.ref.WeakReference

class PreloadContentProvider : ContentProvider() {
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? = null

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun getType(uri: Uri): String? = null

    override fun onCreate(): Boolean {
        return true
    }

    override fun attachInfo(context: Context?, info: ProviderInfo?) {
        super.attachInfo(context, info)

        val app = context as MainApplication

        app.prepare()
    }
}

class MainApplication: Application(), ActivitiesProvider {
    private lateinit var internalCore: Core
    private val activitiesList = mutableListOf<WeakReference<Activity>>()

    val core: Core get() = internalCore

    fun prepare() {
        getAppContext = { this }
        internalCore = Core("latenighthack.com")
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun addActivity(activity: Activity) {
        activitiesList.add(WeakReference(activity))
    }

    override fun allActivities(): List<Activity> {
        return activitiesList.mapNotNull { it.get() }
    }

    override fun removeActivity(activity: Activity) {
        activitiesList.removeIf { it.get() == activity }
    }
}
