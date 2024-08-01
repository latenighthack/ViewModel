package com.latenighthack.viewmodel.list

import kotlinx.coroutines.flow.*

public sealed class Change {
    public data class Inserted(val to: IntRange) : Change()
    public data class Deleted(val from: IntRange) : Change()
    public data class Moved(val from: Int, val to: Int) : Change()
    public data class Refresh(val index: Int) : Change()
    public data object Reloaded : Change()
}

public data class Delta<T>(val items: List<T>, val change: List<Change>) {
    public companion object {
        public fun <T> reloaded(items: List<T>): Delta<T> = Delta(items, listOf(Change.Reloaded))
        public fun <T> empty(): Delta<T> = Delta(emptyList<T>(), listOf(Change.Reloaded))
    }
}

public fun <T> flowAsSingletonList(flow: Flow<T>, predicate: (T) -> Boolean = { true }): Flow<Delta<T>> = flow.map {
    if (predicate(it)) {
        Delta.reloaded(listOf(it))
    } else {
        Delta.reloaded(emptyList())
    }
}

public fun <T> flowListOf(vararg items: T): Flow<Delta<T>> = flowOf(Delta(items.asList(), listOf(Change.Reloaded)))

public fun <T> flowListOf(callback: suspend () -> List<T>): Flow<Delta<T>> {
    return flow {
        val list = callback()

        emit(Delta(list, listOf(Change.Reloaded)))
    }
}

public fun <T> Flow<List<T>>.diffFlowList(): Flow<Delta<T>> = map {
    Delta(it, listOf(Change.Reloaded))
}

public fun <T> List<Flow<T>>.combine(): Flow<List<T>> {
    return recursiveCombine(this, 0)
}

private fun <T> recursiveCombine(list: List<Flow<T>>, offset: Int): Flow<MutableList<T>> {
    if (offset == 0 && list.isEmpty()) {
        return emptyFlow()
    }

    val mappedFlow = list[offset]

    return if (offset == list.size - 1) {
        mappedFlow.map { mutableListOf(it) }
    } else {
        recursiveCombine(list, offset + 1).combine(mappedFlow) { t1, t2 ->
            t1.add(0, t2)

            t1
        }
    }
}
