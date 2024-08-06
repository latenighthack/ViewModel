package com.latenighthack

import com.latenighthack.viewmodel.core.IHomeViewModel
import com.latenighthack.viewmodel.core.Navigator
import com.latenighthack.viewmodel.vue.toParams
import kotlin.js.json

class WebNavigator(private val router: dynamic): Navigator {
    override fun close() {
        router.go(-1)
    }

    override fun navigateTo(home: IHomeViewModel.Args) {
        router.push(json("name" to "home", "params" to home.toParams() ))
    }
}
