package com.latenighthack.viewmodel.core.forwards

import com.latenighthack.viewmodel.common.GlobalDispatcher
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

public class Forwards : com.latenighthack.viewmodel.Forwards()

public class BindingScope : CoroutineScope {
    public val job: CompletableJob = SupervisorJob()

    override val coroutineContext: CoroutineContext =
        job + GlobalDispatcher.main() + CoroutineExceptionHandler { _, exception ->
            println(">>> (binding scope exception) $exception\n${exception.stackTraceToString()}")
        }

    public fun collect(flow: Flow<Any>, collector: (Any) -> Unit) {
        launch {
            flow.collect {
                collector(it)
            }
        }
    }

    public fun cancel() {
        job.cancel()
    }
}
