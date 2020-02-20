package com.sudox.design.viewlist

import androidx.recyclerview.widget.SortedList

/**
 * Пересортировыает предметы в сортированном списке.
 */
fun SortedList<*>.sortItems() {
    beginBatchedUpdates()

    for (i in 0 until size()) {
        recalculatePositionOfItemAt(i)
    }

    endBatchedUpdates()
}