package com.latenighthack.viewmodel

import com.latenighthack.viewmodel.common.GlobalDispatcher
import com.latenighthack.viewmodel.list.Delta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

public class FlowStateCollector<T : ViewModel<U>, U : Any>(
    public val scope: CoroutineScope,
    public val items: Flow<Delta<T>>
) {
    private var currentVms: List<T>? = null
    private var currentState: Array<Any>? = null
    private var currentStateJobs: Array<Job>? = null
    private var job = Job()

    public val size: Int
        get() {
            return currentVms?.size ?: 0
        }

    public fun get(index: Int): T? {
        return currentVms?.get(index)
    }

    @Suppress("UNCHECKED_CAST")
    public fun getState(index: Int): U? {
        return currentState?.get(index) as U?
    }

    public fun start() {
        scope.launch(GlobalDispatcher.main() + job) {
            items.collect {
                currentStateJobs?.apply {
                    forEach { stateJob ->
                        stateJob.cancel()
                    }
                }

                currentVms = it.items
                currentState = Array(it.items.size) { i ->
                    val vm = it.items[i]

                    vm.initialState
                }
                currentStateJobs = Array(it.items.size) { i ->
                    val vm = it.items[i]
                    val stateJob = Job()

                    scope.launch(GlobalDispatcher.main() + job + stateJob) {
                        vm.state.collect {
                            currentState!![i] = it
                        }
                    }

                    stateJob
                }
            }
        }
    }

    public fun stop() {
        job.complete()

        currentStateJobs?.apply {
            forEach { stateJob ->
                stateJob.cancel()
            }
        }

        currentVms = null
        currentState = null
        currentStateJobs = null
    }
}
