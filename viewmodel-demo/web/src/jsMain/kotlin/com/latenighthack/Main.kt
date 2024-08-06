package com.latenighthack

import com.latenighthack.viewmodel.common.ViewModelReporter
import com.latenighthack.viewmodel.core.Core
import com.latenighthack.viewmodel.startup
import com.latenighthack.viewmodel.vue.ViewModelVueCreator
import kotlinx.browser.window
import org.w3c.dom.get

@JsExport
fun main(
    serverPath: String,
    isSecure: Boolean,
    router: dynamic,
    callback: dynamic
) {
    val core = Core()
    val vueModels = ViewModelVueCreator(
        ViewModelCreator(),
        object : ViewModelReporter {
            override fun trackNavigation(screen: String) {
            }

            override fun trackAction(
                screen: String,
                parent: String,
                noun: String,
                verb: String,
                success: Boolean,
                duration: Long?,
                error: Throwable?
            ) {
            }
        }
    )

    window.asDynamic()["vueModels"] = vueModels

    startup(core, WebNavigator(router)) {
        callback()
    }
}
