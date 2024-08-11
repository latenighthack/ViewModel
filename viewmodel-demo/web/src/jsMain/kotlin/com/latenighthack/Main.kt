package com.latenighthack

import com.latenighthack.ktstore.createStoreDelegate
import com.latenighthack.viewmodel.common.ViewModelReporter
import com.latenighthack.viewmodel.core.Core
import com.latenighthack.viewmodel.startup
import com.latenighthack.viewmodel.vue.ViewModelVueCreator
import kotlinx.browser.window
import org.w3c.dom.get

@OptIn(ExperimentalJsExport::class)
@JsExport
fun main(
    serverPath: String,
    isSecure: Boolean,
    router: dynamic,
    callback: dynamic
) {
    val core = Core("latenighthack.com")
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
    val navigator = WebNavigator(router)

    callback(core, navigator, vueModels)

    startup(core, navigator) {
    }
}
