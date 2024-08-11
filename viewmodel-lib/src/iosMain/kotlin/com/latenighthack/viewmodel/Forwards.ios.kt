package com.latenighthack.viewmodel

import com.latenighthack.viewmodel.common.BindingScope

public actual open class Forwards {
    public fun bindingScope(): BindingScope = TODO()

    public fun <T : ViewModel<U>, U> flowListDataSource(): FlowListDataSource<T, U> = TODO()

    public fun <T : ViewModel<U>, U: Any> flowStateCollector(): FlowStateCollector<T, U> = TODO()
}
