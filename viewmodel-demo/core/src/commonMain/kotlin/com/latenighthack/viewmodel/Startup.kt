package com.latenighthack.viewmodel

import com.latenighthack.viewmodel.core.Core
import com.latenighthack.viewmodel.core.IHomeViewModel
import com.latenighthack.viewmodel.core.Navigator
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
public fun startup(core: Core, navigator: Navigator, ready: () -> Unit) {
    GlobalScope.launch {
        core.start()

        navigator.navigateTo(IHomeViewModel.Args())

        ready()
    }
}
