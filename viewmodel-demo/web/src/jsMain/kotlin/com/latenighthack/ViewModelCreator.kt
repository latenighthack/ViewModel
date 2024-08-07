package com.latenighthack

import com.latenighthack.viewmodel.common.BindingScope
import com.latenighthack.viewmodel.core.Core
import com.latenighthack.viewmodel.core.HomeViewModel
import com.latenighthack.viewmodel.core.IHomeViewModel
import com.latenighthack.viewmodel.core.Navigator
import com.latenighthack.viewmodel.vue.IViewModelCreator

class ViewModelCreator: IViewModelCreator {
    override fun homeViewModel(
        args: IHomeViewModel.Args,
        resolver: Core,
        navigator: Navigator,
        bindingScope: BindingScope,
        extras: dynamic
    ) = HomeViewModel(args, resolver.simpleStore)
}