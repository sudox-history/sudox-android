package ru.sudox.android.core.ui.lists

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.MenuRes
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.sudox.android.core.ui.R
import ru.sudox.android.core.ui.lists.holder.LoaderViewHolder
import ru.sudox.android.core.ui.lists.holder.SectionHeaderViewHolder
import ru.sudox.android.core.ui.lists.model.SECTION_TYPE_CHANGED_FLAG
import ru.sudox.android.core.ui.lists.model.SectionVO
import ru.sudox.android.core.ui.popup.CustomPopupMenuBuilder
import ru.sudox.simplelists.BasicListHolder
import ru.sudox.simplelists.callbacks.ItemDiffCallback
import ru.sudox.simplelists.callbacks.OffsetItemUpdateCallback
import ru.sudox.simplelists.loadable.LOADER_VIEW_TYPE
import ru.sudox.simplelists.model.BasicListItem
import ru.sudox.simplelists.sectioned.SectionedListAdapter

const val HEADER_VIEW_TYPE = -2

/**
 * Адаптер для списка экрана, поделенного на секции.
 */
abstract class SectionedScreenListAdapter(
    private val context: Context
) : SectionedListAdapter() {

    private val menuInflater = MenuInflater(context)

    @VisibleForTesting
    val sectionsWithHeaders = HashSet<Int>()

    @VisibleForTesting
    val menuCache = HashMap<Int, CustomPopupMenuBuilder>()
    var loadSectionItems: ((SectionVO) -> (List<BasicListItem<*>>?))? = null


    /**
     * Перезагружает секцию
     *
     * @param vo ViewObject секции
     * @param updateId Функция для обновления ID
     * @param needUpdate Нужно обновить элемент секции?
     * @param needLoad Нужно загрузить данные?
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun reloadSection(vo: SectionVO, updateId: ((SectionVO) -> Unit)?, needUpdate: Boolean, needLoad: Boolean, payload: Any?) {
        val section = sections[vo.order]

        if (section!!.isLoading) {
            toggleLoading(vo.order, false)
            forceLoadingDisabledCallback!!.invoke(vo.order)
        }

        updateId?.invoke(vo)

        if (needLoad) {
            // Пробуем загрузить элементы ...
            val items = loadSectionItems!!(vo)

            if (items == null) {
                removeItemsFromSection(vo.order, 0, getSectionItemsCount(vo.order))
            } else {
                changeSectionItems(vo.order, items, false)
            }
        } else {
            removeItemsFromSection(vo.order, 0, getSectionItemsCount(vo.order))
        }

        if (needUpdate) {
            notifyItemChanged(section.startPosition, payload)
        }
    }

    /**
     * Добавляет секцию в список
     *
     * @param order Порядковый номер секции
     * @param vo ViewObject секции (если null, то секция без заголовка)
     */
    fun addSection(order: Int, vo: SectionVO? = null) {
        addSection(order)

        if (vo != null) {
            sectionsWithHeaders.add(order)
            vo.order = order

            if (vo.typesMenuRes != 0) {
                cacheSectionMenu(vo.typesMenuRes, vo.selectedTypeId)
            }

            if (vo.sortsMenuRes != 0) {
                cacheSectionMenu(vo.sortsMenuRes, vo.selectedSortId)
            }

            super.addItemsToSection(order, 0, listOf(BasicListItem(HEADER_VIEW_TYPE, vo)))

            loadSectionItems?.invoke(vo)?.let {
                addItemsToSection(order, 0, it)
            }
        }
    }

    override fun removeSection(order: Int) {
        if (sectionsWithHeaders.contains(order)) {
            sectionsWithHeaders.remove(order)

            val section = sections[order]
            val entry = currentItems[section!!.startPosition].viewObject as SectionVO

            if (entry.typesMenuRes != 0) {
                menuCache.remove(entry.typesMenuRes)
            }
        }

        super.removeSection(order)
    }

    override fun addItemsToSection(order: Int, position: Int, items: List<BasicListItem<*>>) {
        var insertPosition = position

        if (sectionsWithHeaders.contains(order)) {
            insertPosition++
        }

        super.addItemsToSection(order, insertPosition, items)
    }

    override fun removeItemsFromSection(order: Int, position: Int, count: Int) {
        var removingPosition = position

        if (sectionsWithHeaders.contains(order)) {
            removingPosition++
        }

        super.removeItemsFromSection(order, removingPosition, count)
    }

    override fun getItemsFromSection(order: Int, position: Int, count: Int, copy: Boolean): MutableList<BasicListItem<*>> {
        var convertedPosition = position

        if (sectionsWithHeaders.contains(order)) {
            convertedPosition++
        }

        return super.getItemsFromSection(order, convertedPosition, count, copy)
    }

    override fun updateSectionItem(order: Int, position: Int, item: BasicListItem<*>) {
        var updatePosition = position

        if (sectionsWithHeaders.contains(order)) {
            updatePosition++
        }

        super.updateSectionItem(order, updatePosition, item)
    }

    override fun changeSectionItems(order: Int, newList: List<BasicListItem<*>>?, detectMoves: Boolean) {
        val section = sections[order]!!
        var fromIndex = section.startPosition
        var toIndex = fromIndex + section.itemsCount
        val prevCount = getSectionItemsCount(order)

        if (sectionsWithHeaders.contains(order)) {
            fromIndex++
        }

        if (section.isLoading) {
            toIndex--
        }

        val oldList = currentItems.subList(fromIndex, toIndex)
        val result = DiffUtil.calculateDiff(ItemDiffCallback(ArrayList(oldList), newList) { old, new ->
            new.viewAnimationState = old.viewAnimationState
        }, detectMoves)

        oldList.clear()

        if (newList != null) {
            oldList.addAll(newList)
        }

        section.itemsCount -= prevCount
        section.itemsCount += newList?.size ?: 0

        val added = getSectionItemsCount(order) - prevCount

        sections.forEach { (currentOrder, section) ->
            if (currentOrder > order) {
                section.startPosition += added
            }
        }

        result.dispatchUpdatesTo(OffsetItemUpdateCallback(this, fromIndex))
    }

    override fun toggleLoading(order: Int, toggle: Boolean) {
        val section = sections[order]!!.apply {
            isLoading = toggle
        }

        if (toggle) {
            super.addItemsToSection(order, section.itemsCount, listOf(BasicListItem(LOADER_VIEW_TYPE, null)))
        } else {
            super.removeItemsFromSection(order, section.itemsCount - 1, 1)
        }
    }

    override fun isSectionInInitialLoading(order: Int): Boolean {
        val section = sections[order]!!
        var itemsCount = section.itemsCount

        if (sectionsWithHeaders.contains(order)) {
            itemsCount--
        }

        return section.isLoading && itemsCount == 1
    }

    override fun getSectionItemsCount(order: Int): Int {
        var count = super.getSectionItemsCount(order)

        if (sectionsWithHeaders.contains(order)) {
            count--
        }

        return count
    }

    private fun cacheSectionMenu(@MenuRes menuResId: Int, selectedId: Int) {
        val menu = CustomPopupMenuBuilder(context)
        menuInflater.inflate(menuResId, menu)
        menu.selectedItemId = selectedId

        // Кешируем меню для избежания чтения меню из XML во время скролла
        menuCache[menuResId] = menu
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.layoutAnimation = null
        recyclerView.itemAnimator = null
    }

    override fun createItemViewHolder(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BasicListHolder<*> {
        return if (viewType != HEADER_VIEW_TYPE) {
            createOtherViewHolder(context, inflater, parent, viewType)
        } else {
            val view = inflater.inflate(R.layout.item_section_header, parent, false)
            val holder = SectionHeaderViewHolder(view, { vo, id ->
                reloadSection(vo, { vo.selectedTypeId = id }, true, needLoad = true, payload = listOf(SECTION_TYPE_CHANGED_FLAG))
            }, { vo, id ->
                reloadSection(vo, { vo.selectedSortId = id }, false, needLoad = true, payload = null)
            }, {
                it.isCollapsed = !it.isCollapsed
                reloadSection(it, null, false, !it.isCollapsed, null)
            }) { menuCache[it]!! }

            holder
        }
    }

    override fun createLoaderViewHolder(context: Context, inflater: LayoutInflater, parent: ViewGroup): BasicListHolder<*> =
        LoaderViewHolder(ProgressBar(context))


    /**
     * Возвращает Holder необособленного элемента списка.
     *
     * @param context Контекст приложения/активности
     * @param inflater LayoutInflater для получения разметки
     * @param parent Родительская View
     * @param viewType Тип View, которую нужно создать.
     */
    abstract fun createOtherViewHolder(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BasicListHolder<*>
}