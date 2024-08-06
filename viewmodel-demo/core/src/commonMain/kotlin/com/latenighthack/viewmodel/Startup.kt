package com.latenighthack.viewmodel

import com.latenighthack.viewmodel.core.Core
import com.latenighthack.viewmodel.core.IHomeViewModel
import com.latenighthack.viewmodel.core.Navigator

public fun startup(core: Core, navigator: Navigator, ready: () -> Unit) {
    navigator.navigateTo(IHomeViewModel.Args())

    ready()
}
