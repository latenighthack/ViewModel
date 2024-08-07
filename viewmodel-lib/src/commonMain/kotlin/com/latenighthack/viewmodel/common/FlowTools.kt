package com.latenighthack.viewmodel.common

import com.latenighthack.viewmodel.ViewModel
import com.latenighthack.viewmodel.list.Delta
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

public class CFlow<T>(private val origin: Flow<T>) : Flow<T> by origin {
    public fun watch(block: (T) -> Unit): FlowCloseable {
        val job = Job()

        onEach {
            block(it)
        }.launchIn(CoroutineScope(GlobalDispatcher.main() + job))

        return object : FlowCloseable {
            override fun close() {
                job.cancel()
            }
        }
    }
}


public interface FlowCloseable {
    public fun close()
}

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

public fun <T> Flow<T>.wrap(): CFlow<T> = CFlow(this)

public fun <T> watchFlow(flow: Flow<T>, block: (T) -> Unit): FlowCloseable = CFlow(flow).watch(block)

public fun <T : ViewModel<U>, U> viewModelAsSingletonList(viewModel: T, predicate: (U) -> Boolean = { true }): Flow<Delta<T>> = flow {
    emit(
        if (predicate(viewModel.initialState)) {
            Delta.reloaded(listOf(viewModel))
        } else {
            Delta.reloaded(emptyList())
        }
    )

    emitAll(viewModel.state.map {
        if (predicate(it)) {
            Delta.reloaded(listOf(viewModel))
        } else {
            Delta.reloaded(emptyList())
        }
    })
}
