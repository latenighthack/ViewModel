package com.latenighthack.viewmodel.list

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


public fun <T> Flow<Delta<T>>.splitBy(predicate: (T) -> Boolean): Pair<Flow<Delta<T>>, Flow<Delta<T>>> = separateBy {
    if (predicate(it)) {
        0
    } else {
        1
    }
}.let {
    Pair(it.get(0), it.get(1))
}

public interface DifferentiatedFlowList<T, U> {
    public fun get(key: U): Flow<Delta<T>>
}

private class InternalDifferentiatedFlowList<T, U>(
    private val originalFlow: Flow<Delta<T>>,
    private val differentiator: (T) -> U
) : DifferentiatedFlowList<T, U> {

    val differentiatedFlow = originalFlow
        .map {
            val itemsMap = mutableMapOf<U, MutableList<Pair<Int, T>>>()
            val items = it.items.mapIndexed { index, item -> Pair(index, item) }

            // split indexed-items into categories
            for (item in items) {
                val category = differentiator(item.second)
                val categoryItems = itemsMap[category] ?: mutableListOf()

                categoryItems.add(item)

                itemsMap[category] = categoryItems
            }

            // todo: split changeset into categories
            // todo: determine offsets
            // todo: reindex changeset
            // todo: reindex items

            itemsMap.mapValues { (_, items) ->
                Delta.reloaded(items.map { it.second })
            }
        }

    override fun get(key: U): Flow<Delta<T>> = differentiatedFlow.map {
        it[key] ?: Delta.empty()
    }
}

public fun <T, U> Flow<Delta<T>>.separateBy(differentiator: (T) -> U): DifferentiatedFlowList<T, U>
= InternalDifferentiatedFlowList(this, differentiator)

public fun <T> Flow<Delta<T>>.filterItems(predicate: (T) -> Boolean): Flow<Delta<T>> = map {
    Delta.reloaded(it.items.filter(predicate))
}
