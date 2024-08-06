package com.latenighthack.viewmodel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate

public abstract class StatefulViewModel<State>(initialState: State) : ViewModel<State> {
    override val initialState: State = initialState

    private val internalState = MutableStateFlow(initialState)

    override val state: Flow<State> get() = internalState

    protected suspend fun update(updater: suspend State.() -> State) {
        internalState.getAndUpdate {
            it.updater()
        }
    }

    protected suspend fun withState(inspector: suspend (State) -> Unit) {
        inspector(internalState.value)
    }
}
