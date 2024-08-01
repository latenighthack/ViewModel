package com.latenighthack.viewmodel.list

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan

private class ConcatList<T : Any, U : Any>(private val list1: List<T>, private val list2: List<U>) : List<Any> {
    override fun contains(element: Any): Boolean = TODO("unsupported")

    override fun containsAll(elements: Collection<Any>): Boolean = TODO("unsupported")

    override fun get(index: Int): Any = if (index < list1.size) list1[index] else list2[index - list1.size]

    override fun indexOf(element: Any): Int = TODO("unsupported")

    override fun isEmpty(): Boolean = list1.isEmpty() && list2.isEmpty()

    override fun iterator(): Iterator<Any> = TODO("unsupported")

    override fun lastIndexOf(element: Any): Int = TODO("unsupported")

    override fun listIterator(): ListIterator<Any> = TODO("unsupported")

    override fun listIterator(index: Int): ListIterator<Any> = TODO("unsupported")

    override fun subList(fromIndex: Int, toIndex: Int): List<Any> = when {
        toIndex < list1.size -> list1.subList(fromIndex, toIndex)
        fromIndex >= list1.size -> list2.subList(fromIndex - list1.size, toIndex - list1.size)
        else -> ConcatList(list1.subList(fromIndex, list1.size), list2.subList(0, toIndex))
    }

    override val size: Int = list1.size + list2.size
}

public fun <T : Any, U : Any> Flow<Delta<T>>.concat(other: Flow<Delta<U>>): Flow<Delta<Any>> {
    var previousDelta1: Delta<*>? = null
    var previousDelta2: Delta<*>? = null

    return combine(other) { delta1, delta2 ->
        when {
            // delta2 is the "real" change
            delta1 == previousDelta1 -> {
                previousDelta2 = delta2

                val offset = delta1.items.size

                Delta(ConcatList(delta1.items, delta2.items), delta2.change.map {
                    when (it) {
                        is Change.Inserted -> Change.Inserted(
                            IntRange(
                                it.to.start + offset,
                                it.to.endInclusive + offset
                            )
                        )

                        is Change.Deleted -> Change.Deleted(
                            IntRange(
                                it.from.start + offset,
                                it.from.endInclusive + offset
                            )
                        )

                        is Change.Moved -> Change.Moved(it.from + offset, it.to + offset)
                        is Change.Refresh -> Change.Refresh(it.index + offset)
                        is Change.Reloaded -> Change.Reloaded
                    }
                })
            }
            // delta1 is the "real" change
            delta2 == previousDelta2 -> {
                previousDelta1 = delta1

                Delta(ConcatList(delta1.items, delta2.items), delta1.change)
            }
            // we are in the initial state, both changesets are "real"
            else -> {
                previousDelta1 = delta1
                previousDelta2 = delta2

                // skip doing anything fancy, just reload (it's the first time anyway)
                Delta(ConcatList(delta1.items, delta2.items), listOf(Change.Reloaded))
            }
        }
    }
}

public fun <T : Any, U : Any> Flow<Delta<T>>.header(header: U): Flow<Delta<Any>> {
    return map { delta ->
        if (delta.items.isNotEmpty()) {
            Delta(ConcatList(listOf(header), delta.items), listOf(Change.Reloaded))
        } else {
            Delta(delta.items as List<Any>, delta.change)
        }
    }
}

public fun <T : Any, U : Any> Flow<Delta<T>>.headerIfTrue(header: U, condition: Flow<Boolean>): Flow<Delta<Any>> {
    return combine(condition) { delta, conditionValue ->
        if (conditionValue) {
            Delta(ConcatList(listOf(header), delta.items), listOf(Change.Reloaded))
        } else {
            Delta(delta.items as List<Any>, delta.change)
        }
    }
}

public fun <T : Any, U : Any> Flow<Delta<T>>.orEmpty(emptyItem: U): Flow<Delta<Any>> {
    return this.scan(Delta(emptyList(), listOf(Change.Reloaded))) { accumulator, delta ->
        if (delta.items.isEmpty()) {
            Delta(listOf(emptyItem) as List<Any>, listOf(Change.Reloaded))
        } else {
            if (accumulator.items.size == 1 && accumulator.items[0] == emptyItem) {
                if (delta.change.size == 1 && delta.change[0] == Change.Reloaded) {
                    Delta(delta.items as List<Any>, listOf(Change.Reloaded))
                } else {
                    Delta(delta.items as List<Any>, listOf(Change.Deleted(IntRange(0, 0))).plus(delta.change))
                }
            } else {
                Delta(delta.items as List<Any>, delta.change)
            }
        }
    }
}

public class HeaderItemPlaceholder

public class EmptyItemPlaceholder
