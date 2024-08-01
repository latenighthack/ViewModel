package com.latenighthack.viewmodel

import kotlinx.coroutines.flow.Flow
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

@OptIn(ExperimentalJsExport::class)
@JsExport
public interface ViewModel<State> {
    @JsName("initialState")
    public val initialState: State

    @Suppress("NON_EXPORTABLE_TYPE")
    @JsName("state")
    public val state: Flow<State>
}

public interface NavigableViewModel<State, Args : NavigatorArgs> : ViewModel<State> {
    public val args: Args
}
