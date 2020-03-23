package com.sudox.design.viewlist

import androidx.recyclerview.widget.SortedList

/**
 * Кэллбэк сортировного списка с реализованной поддержкой ViewListCallback
 *
 * @param viewListAdapter Адаптер сортированного списка
 * @param headerType Тип заголовка (нужен для поиска позиций вставки элемента)
 * @param offset Отступ от данной позиции при добавлении/удалении (работает только если нельзя создать шапку)
 */
abstract class ViewListCallback<T>(
        val viewListAdapter: ViewListAdapter<*>,
        val headerType: Int = 0,
        val offset: Int = 0
) : SortedList.Callback<T>() {

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(item1: T, item2: T): Boolean {
        return item1 === item2
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        if (viewListAdapter.canCreateHeaderOrFooter()) {
            viewListAdapter.notifyItemMovedAfterHeader(headerType, fromPosition, toPosition)
        } else {
            viewListAdapter.notifyItemMoved(fromPosition + offset, toPosition + offset)
        }
    }

    override fun onChanged(position: Int, count: Int) {
        if (viewListAdapter.canCreateHeaderOrFooter()) {
            viewListAdapter.notifyItemRangeChangedAfterHeader(headerType, position, count)
        } else {
            viewListAdapter.notifyItemRangeChanged(position + offset, count)
        }
    }

    override fun onInserted(position: Int, count: Int) {
        if (viewListAdapter.canCreateHeaderOrFooter()) {
            viewListAdapter.notifyItemRangeInsertedAfterHeader(headerType, position, count)
        } else {
            if (viewListAdapter.itemCount == 2) {
                viewListAdapter.notifyItemChanged(0)
            }

            viewListAdapter.notifyItemRangeInserted(position + offset, count)
        }
    }

    override fun onRemoved(position: Int, count: Int) {
        if (viewListAdapter.canCreateHeaderOrFooter()) {
            viewListAdapter.notifyItemRangeRemovedAfterHeader(headerType, position, count)
        } else {
            viewListAdapter.notifyItemRangeRemoved(position + offset, count)
        }
    }
}