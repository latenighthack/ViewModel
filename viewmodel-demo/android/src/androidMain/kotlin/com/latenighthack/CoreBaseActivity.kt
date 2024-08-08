package com.latenighthack

import com.latenighthack.viewmodel.BaseActivity
import com.latenighthack.viewmodel.NavigableViewModel
import com.latenighthack.viewmodel.NavigatorArgs
import com.latenighthack.viewmodel.core.Core

abstract class CoreBaseActivity<ViewModelType: NavigableViewModel<StateType, ArgsType>, StateType, ArgsType: NavigatorArgs>: BaseActivity<ViewModelType, StateType, ArgsType>() {
    protected val core: Core get() = (application as MainApplication).core
}
