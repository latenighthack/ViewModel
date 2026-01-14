package com.latenighthack.viewmodel.list

public fun <T> Delta<T>.update(moveToTop: Boolean = false, updatedItem: T, predicate: (T) -> Boolean = { t -> t == updatedItem }): Delta<T> {
    val oldItems = items
    val oldIndex = oldItems.indexOfFirst(predicate)

    return if (moveToTop) {
        if (oldIndex >= 0) {
            val removed = oldItems.filterIndexed { index, _ -> index != oldIndex }
            val updated = listOf(updatedItem) + removed

            if (oldIndex == 0) {
                Delta(updated, listOf(Change.Refresh(0)))
            } else {
                Delta(updated, listOf(Change.Moved(oldIndex, 0)))
            }
        } else {
            val updated = listOf(updatedItem) + oldItems

            Delta(updated, listOf(Change.Inserted(IntRange(0, 0))))
        }
    } else {
        val removed = oldItems.filterIndexed { index, _ -> index != oldIndex }
        val updated = removed + updatedItem

        if (oldIndex >= 0) {
            Delta(updated, listOf(Change.Refresh(oldIndex)))
        } else {
            Delta(updated, listOf(Change.Inserted(IntRange(removed.size, removed.size))))
        }
    }
}
