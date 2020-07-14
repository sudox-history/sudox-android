package ru.sudox.simplelists.sectioned

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.sudox.simplelists.BasicListHolder
import ru.sudox.simplelists.callbacks.ItemDiffCallback
import ru.sudox.simplelists.callbacks.OffsetItemUpdateCallback
import ru.sudox.simplelists.loadable.LOADER_VIEW_TYPE
import ru.sudox.simplelists.loadable.LoadableListAdapter
import ru.sudox.simplelists.model.BasicListItem
import ru.sudox.simplelists.sectioned.model.ListSection
import java.util.*
import kotlin.collections.HashMap

private const val NESTED_RECYCLER_VIEWS_STATES_KEY = "nested_recycler_views_states"

/**
 * Адаптер для списка с поддержкой разделения по секциям.
 */
abstract class SectionedListAdapter : LoadableListAdapter() {

    @VisibleForTesting
    val sections = HashMap<Int, ListSection>()
    var forceLoadingDisabledCallback: ((Int) -> (Unit))? = null
    val boundViewHolders = Stack<BasicListHolder<*>>()
    var states = HashMap<Int, Parcelable>()

    override fun onViewRecycled(holder: BasicListHolder<*>) {
        super.onViewRecycled(holder)

        if (holder.itemView is RecyclerView) {
            if (holder.absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                saveNestedListState(holder)
            }

            boundViewHolders.remove(holder)
        }
    }

    override fun onBindViewHolder(holder: BasicListHolder<*>, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)

        if (payloads.isEmpty() && holder.itemView is RecyclerView) {
            val iterator = states.iterator()
            var parcelable: Parcelable? = null

            if (iterator.hasNext()) {
                val next = iterator.next()
                val section = sections[next.key]

                if (section == null) {
                    iterator.remove()
                }

                if (section!!.startPosition <= holder.absoluteAdapterPosition) {
                    parcelable = next.value
                }
            }

            if (parcelable != null) {
                holder.itemView.layoutManager!!.onRestoreInstanceState(parcelable)
            }
        }

        boundViewHolders.remove(holder)
        boundViewHolders.push(holder)
    }

    private fun saveNestedListState(holder: BasicListHolder<*>) {
        val recyclerView = holder.itemView as RecyclerView
        val pair = sections.entries.find {
            it.value.startPosition <= holder.absoluteAdapterPosition
        }

        if (pair != null) {
            states[pair.key] = recyclerView.layoutManager!!.onSaveInstanceState()!!
        }
    }

    /**
     * Сохраняет состояние адаптера и его компонентов.
     *
     * @param outState Bundle, в который будет сохранено состояние.
     */
    fun saveInstanceState(outState: Bundle) {
        for (holder in boundViewHolders) {
            if (holder.itemView is RecyclerView) {
                saveNestedListState(holder)
            }
        }

        outState.putSerializable(NESTED_RECYCLER_VIEWS_STATES_KEY, states)
    }

    /**
     * Восстанавливает состояние адаптера и его компонентов
     *
     * @param savedInstanceState Bundle, с которого будет произведено восстановление
     */
    @Suppress("UNCHECKED_CAST")
    fun restoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            states = savedInstanceState.getSerializable(NESTED_RECYCLER_VIEWS_STATES_KEY) as HashMap<Int, Parcelable>
        }
    }

    /**
     * Добавляет секцию в список.
     *
     * @param order Порядковый номер секции.
     */
    fun addSection(order: Int) {
        sections[order] = ListSection(false, 0, 0)
    }

    /**
     * Удаляет секцию из списка.
     *
     * @param order Порядковый номер секции.
     */
    open fun removeSection(order: Int) {
        val section = sections[order]!!

        if (section.isLoading) {
            forceLoadingDisabledCallback?.invoke(order)
        }

        removeItems(section.startPosition, section.itemsCount)
        sections.remove(order)
        states.remove(order)
    }

    /**
     * Добавляет элементы в секцию
     *
     * @param order Порядковый номер секции
     * @param position Позиция для вставки относительно секции
     * @param items Элементы для вставки
     */
    open fun addItemsToSection(order: Int, position: Int, items: List<BasicListItem<*>>) {
        var sectionStartPosition = 0

        sections.forEach { (sectionOrder, sectionEntry) ->
            if (sectionOrder < order) {
                sectionStartPosition += sectionEntry.itemsCount
            } else {
                sectionEntry.startPosition += items.size
            }
        }

        val section = sections[order]!!.apply {
            startPosition = sectionStartPosition
            itemsCount += items.size
        }

        addItems(section.startPosition + position, items)
    }

    /**
     * Удаляет элементы из секции
     *
     * @param order Порядковый номер секции
     * @param position Позиция для удаления относительно секции
     * @param count Количество элементов для удаления
     */
    open fun removeItemsFromSection(order: Int, position: Int, count: Int) {
        sections.forEach { (sectionOrder, sectionEntry) ->
            if (sectionOrder > order) {
                sectionEntry.startPosition -= count
            }
        }

        val section = sections[order]!!.apply {
            itemsCount -= count
        }

        removeItems(section.startPosition + position, count)
    }

    /**
     * Выдает элементы из секции
     *
     * @param order Порядковый номер секции
     * @param position Позиция относительно секции, с которой будут выдаваться элементы
     * @param count Количество элементов для выдачи
     * @param copy Копировать элементы?
     */
    open fun getItemsFromSection(order: Int, position: Int, count: Int, copy: Boolean): MutableList<BasicListItem<*>> {
        return getItems(sections[order]!!.startPosition + position, count, copy)
    }

    /**
     * Обновляет элемент в секции
     *
     * @param order Порядковый номер секции
     * @param position Позиция относительно секции
     * @param item Обновленная запись.
     */
    open fun updateSectionItem(order: Int, position: Int, item: BasicListItem<*>) {
        updateItem(item, sections[order]!!.startPosition + position)
    }

    /**
     * Осуществляет изменение данных с помощью DiffUtil
     *
     * @param order Порядковый номер секции
     * @param newList Список с новыми данными
     * @param detectMoves Обнаруживать перемещения элементов?
     */
    open fun changeSectionItems(order: Int, newList: List<BasicListItem<*>>?, detectMoves: Boolean) {
        val section = sections[order]!!
        val fromIndex = section.startPosition
        var toIndex = fromIndex + section.itemsCount

        if (section.isLoading) {
            toIndex--
        }

        val oldList = currentItems.subList(fromIndex, toIndex)
        val result = DiffUtil.calculateDiff(ItemDiffCallback(oldList, newList) { old, new ->
            new.viewAnimationState = old.viewAnimationState
        }, detectMoves)

        oldList.clear()

        if (newList != null) {
            oldList.addAll(newList)
        }

        section.itemsCount = newList?.size ?: 0

        if (section.isLoading) {
            section.itemsCount++
        }

        result.dispatchUpdatesTo(OffsetItemUpdateCallback(this, fromIndex))
    }

    /**
     * Включает/выключает загрузку в секции.
     *
     * @param order Порядковый номер секции
     * @param toggle Включить загрузку?
     */
    open fun toggleLoading(order: Int, toggle: Boolean) {
        val section = sections[order]!!.apply {
            isLoading = toggle
        }

        if (toggle) {
            addItemsToSection(order, section.itemsCount, listOf(BasicListItem(LOADER_VIEW_TYPE, null)))
        } else {
            removeItemsFromSection(order, section.itemsCount - 1, 1)
        }
    }

    /**
     * Возвращает количество элементов в секции.
     * Загрузчик не входит в число элементов секции.
     *
     * @param order Порядковый номер секции.
     */
    open fun getSectionItemsCount(order: Int): Int {
        val section = sections[order]!!
        var count = section.itemsCount

        if (section.isLoading) {
            count--
        }

        return count
    }

    /**
     * Секция находится в начальной загрузке?
     * При чистой загрузке не рекомендуется добавлять элементы в список.
     *
     * @param order Порядковый номер секции.
     */
    open fun isSectionInInitialLoading(order: Int): Boolean {
        val section = sections[order]!!
        return section.isLoading && section.itemsCount == 1
    }
}