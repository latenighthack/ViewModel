package com.latenighthack.viewmodel.common

import kotlin.coroutines.CoroutineContext

public expect object GlobalDispatcher {
    public fun main(): CoroutineContext
}
