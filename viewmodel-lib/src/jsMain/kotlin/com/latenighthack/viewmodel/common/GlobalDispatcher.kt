package com.latenighthack.viewmodel.common

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

public actual object GlobalDispatcher {
    public actual fun main(): CoroutineContext {
        return Dispatchers.Main
    }
}
