package com.latenighthack.viewmodel.common

import kotlin.coroutines.CoroutineContext

public actual object GlobalDispatcher {
    private val mainQueue = MainQueueDispatcher()

    public actual fun main(): CoroutineContext {
        return mainQueue
    }
}
