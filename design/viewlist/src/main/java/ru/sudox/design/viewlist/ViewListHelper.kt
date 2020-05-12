package ru.sudox.design.viewlist

import androidx.recyclerview.widget.SortedList

/**
 * Пересортировыает предметы в сортированном списке.
 */
@Deprecated(message = "Replace by FlexibleAdapter")
fun SortedList<*>.sortItems() {
    beginBatchedUpdates()

    for (i in 0 until size()) {
        recalculatePositionOfItemAt(i)
    }

    endBatchedUpdates()
}