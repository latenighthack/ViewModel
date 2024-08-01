@file:OptIn(ExperimentalNativeApi::class)

package com.latenighthack.viewmodel

import com.latenighthack.viewmodel.common.FlowCloseable
import com.latenighthack.viewmodel.common.GlobalDispatcher
import com.latenighthack.viewmodel.list.Change
import com.latenighthack.viewmodel.list.Delta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import platform.Foundation.NSIndexPath
import platform.UIKit.*
import kotlin.experimental.ExperimentalNativeApi
import kotlin.math.min
import kotlin.native.ref.WeakReference

public abstract class FlowListDataSource<T : ViewModel<U>, U>(
    public val scope: CoroutineScope,
    public val items: List<Flow<Delta<T>>>
) {
    private var currentItems: List<List<T>>? = null

    public abstract fun cellStateDidChange(cell: UITableViewCell, model: ViewModel<Any>, state: Any)
    public abstract fun collectionViewCellStateDidChange(cell: UICollectionViewCell, model: ViewModel<Any>, state: Any)

    public fun attach(cell: UITableViewCell, model: ViewModel<Any>): FlowCloseable {
        cellStateDidChange(cell, model, model.initialState)

        val job = scope.launch(GlobalDispatcher.main()) {
            model.state
                .collect {
                    cellStateDidChange(cell, model, it)
                }
        }

        return object : FlowCloseable {
            override fun close() {
                job.cancel()
            }
        }
    }

    public fun attach(collectionViewCell: UICollectionViewCell, model: ViewModel<Any>): FlowCloseable {
        collectionViewCellStateDidChange(collectionViewCell, model, model.initialState)

        val job = scope.launch(GlobalDispatcher.main()) {
            model.state
                .collect {
                    collectionViewCellStateDidChange(collectionViewCell, model, it)
                }
        }

        return object : FlowCloseable {
            override fun close() {
                job.cancel()
            }
        }
    }

    public data class SectionedChange(val section: Int, val change: Change)

    private data class SectionedDelta<T>(val items: List<List<T>>, val change: List<SectionedChange>)

    private fun flattenItems(): Flow<SectionedDelta<T>> {
        val sectionCount = items.size

        data class SectionedDeltaState<T>(val observedSections: Set<Int>, val delta: SectionedDelta<T>)

        return merge(
            *items
                .mapIndexed { index, item ->
                    item.map { delta ->
                        Pair(index, delta)
                    }
                }
                .toTypedArray()
        )
            .scan<Pair<Int, Delta<T>>, SectionedDeltaState<T>>(
                SectionedDeltaState(
                    emptySet(),
                    SectionedDelta(items.map { emptyList() }, listOf())
                )
            ) { state, b ->
                val a = state.delta
                val index = b.first
                val sectionedChanges = b.second.change.map {
                    SectionedChange(index, it)
                }
                val groupedItems = a.items.mapIndexed { groupIndex, items ->
                    if (groupIndex == index) {
                        b.second.items
                    } else {
                        items
                    }
                }
                val observedSections = state.observedSections + index

                if (observedSections.size < sectionCount) {
                    // hold for now
                    SectionedDeltaState(observedSections, SectionedDelta(groupedItems, emptyList()))
                } else if (observedSections.size == sectionCount && observedSections.size != state.observedSections.size) {
                    // first observation force a reload
                    SectionedDeltaState(
                        observedSections,
                        SectionedDelta(groupedItems, (0 until sectionCount).map { section ->
                            SectionedChange(section, Change.Reloaded)
                        })
                    )
                } else {
                    SectionedDeltaState(observedSections, SectionedDelta(groupedItems, sectionedChanges))
                }
            }
            .mapNotNull { state ->
                val it = state.delta

                if (it.change.isNotEmpty()) {
                    it
                } else {
                    null
                }
            }
    }

    public fun currentItemInSection(section: Int, index: Int): Any? {
        return currentItems?.get(section)?.get(index)
    }

    public fun getItemsInSection(index: Int): Int {
        return currentItems?.get(index)?.size ?: 0
    }

    public fun getSectionCount(): Int {
        return currentItems?.size ?: 0
    }

    @ExperimentalNativeApi
    public fun bind(toTable: UITableView) {
        val weakTableView = WeakReference(toTable)

        scope.launch(GlobalDispatcher.main()) {
            val flattenedSections = flattenItems()

            flattenedSections.collect {
                if (it.change.firstOrNull { sc -> sc.change is Change.Reloaded } != null) {
                    currentItems = it.items
                    weakTableView.get()?.reloadData()
                } else {
                    val tableView = weakTableView.get()

                    if (tableView != null) {
                        tableView.beginUpdates()
                        currentItems = it.items

                        it.change.forEach { change ->
                            when (change.change) {
                                is Change.Deleted -> tableView.deleteRowsAtIndexPaths(change.change.from.map { item ->
                                    NSIndexPath.indexPathForItem(item.toLong(), change.section.toLong())
                                }, 0L)

                                is Change.Inserted -> {
                                    tableView.insertRowsAtIndexPaths(change.change.to.map { item ->
                                        NSIndexPath.indexPathForItem(item.toLong(), change.section.toLong())
                                    }, 0L)

                                    if (change.change.to.first < it.items[change.section].size) {
                                        val upper = min(change.change.to.last, it.items[change.section].size - 1)
                                        val range = IntRange(change.change.to.first, upper)
                                        tableView.reloadRowsAtIndexPaths(range.map { item ->
                                            NSIndexPath.indexPathForItem(item.toLong(), change.section.toLong())
                                        }, 0L)
                                    }
                                }

                                is Change.Moved -> {
                                    tableView.moveRowAtIndexPath(
                                        NSIndexPath.indexPathForItem(
                                            change.change.from.toLong(),
                                            change.section.toLong()
                                        ),
                                        NSIndexPath.indexPathForItem(change.change.to.toLong(), change.section.toLong())
                                    )
                                    tableView.reloadRowsAtIndexPaths(
                                        listOf(
                                            NSIndexPath.indexPathForItem(
                                                change.change.from.toLong(),
                                                change.section.toLong()
                                            ),
                                            NSIndexPath.indexPathForItem(
                                                change.change.to.toLong(),
                                                change.section.toLong()
                                            )
                                        ),
                                        0L
                                    )
                                }

                                is Change.Refresh -> tableView.reloadRowsAtIndexPaths(
                                    listOf(
                                        NSIndexPath.indexPathForItem(
                                            change.change.index.toLong(),
                                            change.section.toLong()
                                        ),
                                    ),
                                    0L,
                                )

                                is Change.Reloaded -> error("Reloads not allowed in change set")
                            }
                        }
                        tableView.endUpdates()
                    }
                }

                afterUpdate()
            }
        }
    }

    public open fun afterUpdate() {
    }

    @ExperimentalNativeApi
    public fun bind(
        collectionView: UICollectionView,
        completionCallback: () -> Unit,
        @Suppress("UNUSED_PARAMETER") breakpointCallback: (String) -> Unit
    ) {
        val flattenedSections = flattenItems()
        val weakCollectionView = WeakReference(collectionView)

        scope.launch(GlobalDispatcher.main()) {
            flattenedSections.collect {
                if (it.change.firstOrNull { it.change is Change.Reloaded } != null) {
                    currentItems = it.items

//                    breakpointCallback(it.change.toString())

                    UIView.performWithoutAnimation {
                        weakCollectionView.get()?.reloadData()
                        afterUpdate()
                    }
                } else {
                    weakCollectionView.get()
                        ?.let { collectionView ->
                            collectionView.performBatchUpdates({
                                currentItems = it.items

                                UIView.performWithoutAnimation {
                                    it.change.forEach { change ->
                                        when (change.change) {
                                            is Change.Deleted -> collectionView.deleteItemsAtIndexPaths(change.change.from.map { item ->
                                                NSIndexPath.indexPathForItem(item.toLong(), change.section.toLong())
                                            })

                                            is Change.Inserted -> {
                                                collectionView.insertItemsAtIndexPaths(change.change.to.map { item ->
                                                    NSIndexPath.indexPathForItem(item.toLong(), change.section.toLong())
                                                })

                                                if (change.change.to.first < it.items[change.section].size) {
                                                    val upper =
                                                        min(change.change.to.last, it.items[change.section].size - 1)
                                                    val range = IntRange(change.change.to.first, upper)
                                                    collectionView.reloadItemsAtIndexPaths(range.map { item ->
                                                        NSIndexPath.indexPathForItem(
                                                            item.toLong(),
                                                            change.section.toLong()
                                                        )
                                                    })
                                                }
                                            }

                                            is Change.Moved -> collectionView.moveItemAtIndexPath(
                                                NSIndexPath.indexPathForItem(
                                                    change.change.from.toLong(),
                                                    change.section.toLong()
                                                ),
                                                NSIndexPath.indexPathForItem(
                                                    change.change.to.toLong(),
                                                    change.section.toLong()
                                                )
                                            )

                                            is Change.Refresh -> collectionView.reloadItemsAtIndexPaths(
                                                listOf(
                                                    NSIndexPath.indexPathForItem(
                                                        change.change.index.toLong(),
                                                        change.section.toLong()
                                                    ),
                                                ),
                                            )

                                            is Change.Reloaded -> error("Reloads not allowed in change set")
                                        }
                                    }
                                }
                            }, {
                            })
                        }
                    afterUpdate()
                }
            }
        }.invokeOnCompletion {
            completionCallback()
        }
    }
}
