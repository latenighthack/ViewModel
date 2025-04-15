package com.latenighthack.viewmodel.list

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

public interface SimpleImmutableListAdapter<T> {
    public fun get(index: Int): T
    public val size: Int
}

public class AdaptedImmutableList<T>(
    private val adapter: SimpleImmutableListAdapter<T>,
    private val offset: Int = 0,
    private val lengthAdjustment: Int = 0
) : List<T> {
    override fun contains(element: T): Boolean = TODO("unsupported")

    override fun containsAll(elements: Collection<T>): Boolean = TODO("unsupported")

    override fun get(index: Int): T = adapter.get(index + offset)

    override fun indexOf(element: T): Int = TODO("unsupported")

    override fun isEmpty(): Boolean = adapter.size <= lengthAdjustment

    override fun iterator(): Iterator<T> = TODO("unsupported")

    override fun lastIndexOf(element: T): Int = TODO("unsupported")

    override fun listIterator(): ListIterator<T> = TODO("unsupported")

    override fun listIterator(index: Int): ListIterator<T> = TODO("unsupported")

    override fun subList(fromIndex: Int, toIndex: Int): List<T> =
        AdaptedImmutableList(adapter, offset + fromIndex, lengthAdjustment - adapter.size - toIndex)

    override val size: Int = adapter.size
}

public class LazyMapList<T, U>(
    private val list: List<T>,
    private val transform: ((T) -> U)
) : List<U> {
    private inner class MappedIterator(private val wrapped: Iterator<T>) : Iterator<U> {
        override fun hasNext(): Boolean = wrapped.hasNext()

        override fun next(): U = transform(wrapped.next())
    }

    override fun contains(element: U): Boolean = TODO("unsupported")

    override fun containsAll(elements: Collection<U>): Boolean = TODO("unsupported")

    override fun get(index: Int): U = transform.invoke(list[index])

    override fun indexOf(element: U): Int = TODO("unsupported")

    override fun isEmpty(): Boolean = list.isEmpty()

    override fun iterator(): Iterator<U> = MappedIterator(list.iterator())

    override fun lastIndexOf(element: U): Int = TODO("unsupported")

    override fun listIterator(): ListIterator<U> = TODO("unsupported")

    override fun listIterator(index: Int): ListIterator<U> = TODO("unsupported")

    override fun subList(fromIndex: Int, toIndex: Int): List<U> =
        LazyMapList(list.subList(fromIndex, toIndex), transform)

    override val size: Int = list.size
}

public class LazyMapPreviousNextList<T, U>(
    private val list: List<T>,
    private val transform: ((T, T?, T?) -> U)
) : List<U> {
    private inner class MappedIterator(private val wrapped: Iterator<T>) : Iterator<U> {
        private var previous: T? = null
        private var nextNext: T? = null
        private var isFirst: Boolean = true
        private var didHaveNext: Boolean = wrapped.hasNext()

        override fun hasNext(): Boolean = didHaveNext

        override fun next(): U {
            if (isFirst) {
                isFirst = false
                nextNext = wrapped.next()
            }

            val oldNext = nextNext

            didHaveNext = wrapped.hasNext()

            nextNext = if (didHaveNext) {
                wrapped.next()
            } else {
                null
            }

            val result = transform(oldNext!!, previous, nextNext)

            previous = oldNext

            return result
        }
    }

    override fun contains(element: U): Boolean = TODO("unsupported")

    override fun containsAll(elements: Collection<U>): Boolean = TODO("unsupported")

    override fun get(index: Int): U = transform.invoke(
        list[index],
        if (index > 0) list[index - 1] else null,
        if (index < list.size - 1) list[index + 1] else null
    )

    override fun indexOf(element: U): Int = TODO("unsupported")

    override fun isEmpty(): Boolean = list.isEmpty()

    override fun iterator(): Iterator<U> = MappedIterator(list.iterator())

    override fun lastIndexOf(element: U): Int = TODO("unsupported")

    override fun listIterator(): ListIterator<U> = TODO("unsupported")

    override fun listIterator(index: Int): ListIterator<U> = TODO("unsupported")

    override fun subList(fromIndex: Int, toIndex: Int): List<U> =
        LazyMapPreviousNextList(list.subList(fromIndex, toIndex), transform)

    override val size: Int = list.size
}

public class LazyMapPreviousList<T, U>(
    private val list: List<T>,
    private val transform: ((T, T?) -> U)
) : List<U> {
    private inner class MappedIterator(private val wrapped: Iterator<T>) : Iterator<U> {
        private var previous: T? = null

        override fun hasNext(): Boolean = wrapped.hasNext()

        override fun next(): U {
            val next = wrapped.next()
            val result = transform(next, previous)

            previous = next

            return result
        }
    }

    override fun contains(element: U): Boolean = TODO("unsupported")

    override fun containsAll(elements: Collection<U>): Boolean = TODO("unsupported")

    override fun get(index: Int): U = transform.invoke(list[index], if (index > 0) list[index - 1] else null)

    override fun indexOf(element: U): Int = TODO("unsupported")

    override fun isEmpty(): Boolean = list.isEmpty()

    override fun iterator(): Iterator<U> = MappedIterator(list.iterator())

    override fun lastIndexOf(element: U): Int = TODO("unsupported")

    override fun listIterator(): ListIterator<U> = TODO("unsupported")

    override fun listIterator(index: Int): ListIterator<U> = TODO("unsupported")

    override fun subList(fromIndex: Int, toIndex: Int): List<U> =
        LazyMapPreviousList(list.subList(fromIndex, toIndex), transform)

    override val size: Int = list.size
}


public fun <T, U> List<T>.lazyMap(transform: (T) -> U): List<U> = LazyMapList(this, transform)

public fun <T, U> Flow<Delta<T>>.lazyMap(transform: (T) -> U): Flow<Delta<U>> =
    map { Delta(LazyMapList(it.items, transform), it.change) }

public fun <T, U> Flow<Delta<T>>.lazyMapPrevious(transform: (current: T, previous: T?) -> U): Flow<Delta<U>> =
    map { Delta(LazyMapPreviousList(it.items, transform), it.change) }

public fun <T, U> Flow<Delta<T>>.lazyMapPreviousNext(transform: (current: T, previous: T?, next: T?) -> U): Flow<Delta<U>> =
    map { Delta(LazyMapPreviousNextList(it.items, transform), it.change) }

public fun <T, U> Flow<Delta<T>>.flowLazyMap(transform: (T) -> U): Flow<Delta<U>> = lazyMap(transform)
