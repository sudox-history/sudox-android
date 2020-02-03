package com.sudox.design.viewlist

import androidx.recyclerview.widget.SortedList

/**
 * Кэллбэк сортировного списка с реализованной поддержкой ViewListCallback
 *
 * @param viewListAdapter Адаптер сортированного списка
 * @param headerType Тип заголовка (нужен для поиска позиций вставки элемента)
 */
abstract class ViewListCallback<T>(
        val viewListAdapter: ViewListAdapter<*>,
        val headerType: Int
) : SortedList.Callback<T>() {

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(item1: T, item2: T): Boolean {
        return item1 === item2
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        viewListAdapter.notifyItemMovedAfterHeader(headerType, fromPosition, toPosition)
    }

    override fun onChanged(position: Int, count: Int) {
        viewListAdapter.notifyItemRangeChangedAfterHeader(headerType, position, count)
    }

    override fun onInserted(position: Int, count: Int) {
        viewListAdapter.notifyItemRangeInsertedAfterHeader(headerType, position, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        viewListAdapter.notifyItemRangeRemovedAfterHeader(headerType, position, count)
    }
}