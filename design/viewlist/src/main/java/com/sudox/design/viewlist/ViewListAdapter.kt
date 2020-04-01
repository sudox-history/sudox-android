package com.sudox.design.viewlist

import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.viewlist.header.ViewListHeaderView
import com.sudox.design.viewlist.vos.ViewListHeaderVO

const val HEADER_VIEW_TYPE = -1
const val FOOTER_VIEW_TYPE = -2
const val LOADER_VIEW_TYPE = -3

abstract class ViewListAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var stickyLetters: Map<Int, String>? = null

    open var nestedRecyclerViews = HashMap<Int, RecyclerView>()
    open var headersVOs: Array<ViewListHeaderVO>? = null
    open var viewList: ViewList? = null

    private var initialTopPadding = -1
    private var initialBottomPadding = -1
    private var dataObserver: RecyclerView.AdapterDataObserver? = null

    /**
     * 1-й аргумент - тип шапки
     * 2-й аргумент - количество элементов до изменения
     * 3-й аргумент - ViewObject шапки
     */
    var sectionChangedCallback: ((Int, Int, ViewListHeaderVO) -> (Unit))? = null
    var sortingTypeChangedCallback: ((Int, Int, ViewListHeaderVO) -> (Unit))? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        dataObserver = object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                viewList!!.let {
                    val orientation = (it.layoutManager as? LinearLayoutManager)?.orientation ?: LinearLayoutManager.VERTICAL

                    if (positionStart == 0 &&
                            ((!it.canScrollVertically(-1) && orientation == LinearLayoutManager.VERTICAL)
                                    || (!it.canScrollHorizontally(-1) && orientation == LinearLayoutManager.HORIZONTAL))) {
                        viewList!!.scrollToPosition(0)
                    }
                }

                stickyLetters = buildStickyLettersMap()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                stickyLetters = buildStickyLettersMap()
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                stickyLetters = buildStickyLettersMap()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                stickyLetters = buildStickyLettersMap()
            }
        }

        registerAdapterDataObserver(dataObserver!!)
        stickyLetters = buildStickyLettersMap()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        if (dataObserver != null) {
            unregisterAdapterDataObserver(dataObserver!!)
            dataObserver = null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HEADER_VIEW_TYPE && getHeadersCount() > 0) {
            HeaderViewHolder(ViewListHeaderView(viewList!!.context).apply {
                syncWithViewList(viewList!!)
            })
        } else if (viewType == FOOTER_VIEW_TYPE && getFooterCount() > 0) {
            FooterViewHolder(AppCompatTextView(ContextThemeWrapper(viewList!!.context, viewList!!.footerTextAppearance)).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            })
        } else if (viewType == LOADER_VIEW_TYPE) {
            LoaderViewHolder(ProgressBar(viewList!!.context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            })
        } else {
            val holder = createItemHolder(parent, viewType)

            if (holder.itemView is RecyclerView) {
                nestedRecyclerViews[getHeaderTypeByItemType(viewType)] = holder.itemView as RecyclerView
            }

            holder
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemView is ViewGroup) {
            (holder.itemView as ViewGroup).clipToPadding = false
        }

        val orientation = (viewList!!.layoutManager as? LinearLayoutManager)?.orientation ?: LinearLayoutManager.VERTICAL

        if (orientation == LinearLayoutManager.VERTICAL) {
            holder.itemView.updatePadding(
                    left = viewList!!.initialPaddingLeft,
                    right = viewList!!.initialPaddingRight
            )
        }

        if (holder is FooterViewHolder) {
            val footer = getFooterText(position)

            if (footer != null) {
                holder.view.text = footer
                return
            }
        } else if (holder is HeaderViewHolder) {
            val vo = getHeaderByPosition(position)

            if (vo != null) {
                holder.view.let {
                    it.itemsVisibilityTogglingCallback = { vo ->
                        vo.isItemsHidden = !vo.isItemsHidden

                        val itemsCount = getItemsCountAfterHeaderConsiderVisibility(vo.type, true) + if (vo.isContentLoading) {
                            1
                        } else {
                            0
                        }

                        vo.isItemsHidden = !vo.isItemsHidden

                        notifyItemChanged(holder.adapterPosition)

                        if (vo.isItemsHidden) {
                            notifyItemRangeRemoved(holder.adapterPosition + 1, itemsCount)
                        } else {
                            notifyItemRangeInserted(holder.adapterPosition + 1, itemsCount)
                        }
                    }

                    it.getItemsCountBeforeChanging = { vo -> getItemsCountAfterHeaderConsiderVisibility(vo.type) }
                    it.itemsSectionChangingCallback = { vo, itemsCountBeforeChanging ->
                        if (!vo.isItemsHidden) {
                            sectionChangedCallback!!(vo.type, itemsCountBeforeChanging, vo)
                        }
                    }

                    it.sortTypeChangingCallback = { vo, itemsCountBeforeChanging ->
                        if (!vo.isItemsHidden) {
                            sortingTypeChangedCallback!!(vo.type, itemsCountBeforeChanging, vo)
                        }
                    }

                    it.vo = vo
                }

                if (initialBottomPadding == -1) {
                    initialBottomPadding = holder.view.paddingBottom
                }

                if (position == 0) {
                    holder.view.updatePadding(top = 0)
                }

                if (!vo.isItemsHidden) {
                    holder.view.updatePadding(bottom = initialBottomPadding - getItemMargin(position + 1) / 2)
                }

                return
            }
        } else if (holder is LoaderViewHolder) {
            return
        }

        if (holder.itemView is RecyclerView) {
            val headerPair = getNearestHeader(position) ?: return
            val headerVO = headerPair.second

            if (headerVO.nestedRecyclerViewParcelable != null) {
                (holder.itemView as RecyclerView)
                        .layoutManager!!
                        .onRestoreInstanceState(headerVO.nestedRecyclerViewParcelable)
            }
        }

        @Suppress("UNCHECKED_CAST")
        bindItemHolder(holder as VH, position).apply {
            if (!canCreateMarginViaDecorators()) {
                val itemMargin = getItemMargin(position) / 2

                if (orientation == LinearLayoutManager.VERTICAL) {
                    holder.itemView.updatePadding(top = itemMargin, bottom = itemMargin)
                } else {
                    holder.itemView.updatePadding(left = itemMargin, right = itemMargin)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getHeaderByPosition(position) != null) {
            HEADER_VIEW_TYPE
        } else if (getFooterText(position) != null) {
            FOOTER_VIEW_TYPE
        } else {
            val nearbyHeaderPair = getNearestHeader(position) ?: return getItemType(position)
            val loaderPosition = nearbyHeaderPair.first + getItemsCountAfterHeaderConsiderVisibility(nearbyHeaderPair.second.type) + 1

            if (!nearbyHeaderPair.second.isItemsHidden && loaderPosition == position) {
                LOADER_VIEW_TYPE
            } else {
                getItemType(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (headersVOs != null) {
            var itemsCount = getHeadersCount()

            headersVOs?.forEachIndexed { type, vo ->
                itemsCount += getItemsCountAfterHeaderConsiderVisibility(type)

                if (vo.isLoaderShowing()) {
                    itemsCount++
                }
            }

            itemsCount + getFooterCount()
        } else {
            getItemsCountAfterHeaderConsiderVisibility(0) + getFooterCount()
        }
    }

    private fun getNearestHeader(position: Int): Pair<Int, ViewListHeaderVO>? {
        for (i in position - 1 downTo 0) {
            val header = getHeaderByPosition(i)

            if (header != null) {
                return Pair(i, header)
            }
        }

        return null
    }

    /**
     * Считает количество элементов, исключая скрытые если это требуется
     *
     * @param type Тип шапки, после которой нужно получить количество элементов
     * @param ignoreHidden Учитывать скрытые элементы?
     * @return Количество элементов
     */
    fun getItemsCountAfterHeaderConsiderVisibility(type: Int, ignoreHidden: Boolean = false): Int {
        if (ignoreHidden || headersVOs == null || (!headersVOs!![type].isItemsHidden && !headersVOs!![type].isInClearLoading)) {
            return getItemsCountAfterHeader(type)
        }

        return 0
    }

    /**
     * Уведомляет RecyclerView о том, что новые данные в измененной секции готовы к отображению
     * P.S.: Отключает загрузку если она в "чистом" режиме!
     *
     * @param headerType Тип заголовка
     * @param itemsCountBeforeChanging Количество предметов после заголовка до изменения
     * @param itemsCount Количество элементов после изменения
     */
    fun notifyChangedSectionDataChanged(headerType: Int, itemsCountBeforeChanging: Int, itemsCount: Int) {
        notifyItemRangeRemovedAfterHeader(headerType, 0, itemsCountBeforeChanging)
        notifyItemRangeInsertedAfterHeader(headerType, 0, itemsCount)
    }

    /**
     * Уведомляет RecyclerView о вставке элемента.
     * При необходимости уведомляет RecyclerView о появлении шапки
     *
     * @param type Тип шапки
     * @param position Позиция относительно шапки, начиная с которой будет вставляться элемент
     * @param itemCount Количество элементов для вставки
     */
    fun notifyItemRangeInsertedAfterHeader(type: Int, position: Int, itemCount: Int) {
        if (headersVOs?.isNotEmpty() == true) {
            var headerPosition = findHeaderPosition(type, itemCount)
            val itemPosition = position + 1

            if (headerPosition == -1) {
                headerPosition = getPositionForNewHeader(type)
                notifyItemInserted(headerPosition)
            }

            notifyItemRangeInserted(itemPosition + headerPosition, itemCount)

            if (headersVOs!![type].isInClearLoading) {
                toggleLoading(type, false)
            }
        } else {
            notifyItemRangeInserted(position, itemCount)
        }

        updateFooters()
    }

    /**
     * Уведомляет RecyclerView об удалении элемента.
     * При необходимости уведомляет RecyclerView об удалении шапки.
     *
     * @param type Тип шапки
     * @param position Позиция относительно шапки, начиная с которой будут удаляться элементы
     * @param itemCount Количество элементов для удаления
     */
    fun notifyItemRangeRemovedAfterHeader(type: Int, position: Int, itemCount: Int) {
        // Cannot be -1, because item created and consequently header also created

        if (headersVOs?.isNotEmpty() == true) {
            val headerPosition = findHeaderPosition(type, itemCount)
            var startPosition = headerPosition + position + 1
            val vo = headersVOs!![type]

            if (vo.isInClearLoading) {
                return
            }

            if (vo.getToggleOptions(viewList!!.context).size == 1 && getItemsCountAfterHeaderConsiderVisibility(type) == 0) {
                notifyItemRemoved(headerPosition)
                startPosition--
            }

            notifyItemRangeRemoved(startPosition, itemCount)
        } else {
            notifyItemRangeRemoved(position, itemCount)
        }

        updateFooters()
    }

    private fun updateFooters() {
        var updatedFooters = 0
        val footersCount = getFooterCount()

        for (i in itemCount - 1 downTo 0) {
            if (getFooterText(i) != null) {
                notifyItemChanged(i)
                updatedFooters++

                if (updatedFooters > footersCount) {
                    break
                }
            }
        }
    }

    /**
     * Уведомляет RecyclerView о изменении данных в элементах
     *
     * @param type Тип шапки
     * @param position Позиция относительно шапки, начиная с которой были изменены элементы
     * @param count Количество измененных элементов
     */
    fun notifyItemRangeChangedAfterHeader(type: Int, position: Int, count: Int) {
        // Cannot be -1, because item created and consequently header also created

        if (headersVOs?.isNotEmpty() == true) {
            val headerPosition = findHeaderPosition(type, itemCount)
            val startPosition = headerPosition + position + 1

            notifyItemRangeChanged(startPosition, count)
        } else {
            notifyItemRangeChanged(position, count)
        }
    }

    /**
     * Включает/выключает загрузку в секции
     *
     * @param type Тип шапки над секцией
     * @param toggle Отобразить загрузку?
     * @param clearLoading Это чистая загрузка? (скрывает все элементы списка)
     */
    fun toggleLoading(type: Int, toggle: Boolean, clearLoading: Boolean = false) {
        val headerPosition = findHeaderPosition(type, itemCount)
        val itemsInSection = getItemsCountAfterHeaderConsiderVisibility(type, !toggle)
        val loaderPosition = headerPosition + itemsInSection + 1

        if (toggle && headersVOs!![type].canShowLoader()) {
            notifyItemInserted(loaderPosition)

            if (clearLoading && itemsInSection > 0) {
                notifyItemRangeRemoved(headerPosition + 1, itemsInSection)
            }
        } else if (!toggle && headersVOs!![type].canHideLoader()) {
            notifyItemRemoved(loaderPosition)
        }

        headersVOs!![type].isContentLoading = toggle
        headersVOs!![type].isInClearLoading = if (toggle) {
            clearLoading
        } else {
            false
        }
    }

    /**
     * Уведомляет RecyclerView о перемещении элемента.
     *
     * @param type Тип шапки
     * @param fromPosition Позиция, относительно шапки, с которой переместился элемент
     * @param toPosition Позиция, относительно шапки, на которую переместился элемент
     */
    fun notifyItemMovedAfterHeader(type: Int, fromPosition: Int, toPosition: Int) {
        if (headersVOs?.isNotEmpty() == true) {
            // Cannot be -1, because item created and consequently header also created

            if (headersVOs!![type].isInClearLoading) {
                return
            }

            val headerPosition = findHeaderPosition(type, itemCount)
            val itemFromPosition = headerPosition + fromPosition + 1
            val itemToPosition = headerPosition + toPosition + 1

            notifyItemMoved(itemFromPosition, itemToPosition)
        } else {
            notifyItemMoved(fromPosition, toPosition)
        }
    }

    /**
     * Ищет позицию шапки, на которой находится указанный текст
     *
     * @param type Тип заголовка для поиска
     * @param removedItemsCount Количество удаленных элементов
     * @result Позиция найденной View, -1 если View не найдена ...
     */
    fun findHeaderPosition(type: Int, removedItemsCount: Int = 0): Int {
        val need = headersVOs!![type]

        for (i in 0 until itemCount + removedItemsCount) {
            val vo = getHeaderByPosition(i)

            if (vo == need) {
                return i
            }
        }

        return -1
    }

    /**
     * Пересчитывает позицию относительно адаптера в позицию, относительно шапки (считает количество предшествующих
     * элементов после шапки)
     *
     * @param position Позиция для пересчета
     * @return Позиция, относительно шапки
     */
    fun recalculatePositionRelativeHeader(position: Int): Int {
        if (getHeadersCount() == 0) {
            return position
        }

        for (i in position downTo 0) {
            val header = getHeaderByPosition(i)

            if (header != null) {
                return position - i - 1
            }
        }

        return position
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder.itemView is RecyclerView) {
            val headerPair = getNearestHeader(holder.adapterPosition) ?: return

            headerPair.second.nestedRecyclerViewParcelable = (holder.itemView as RecyclerView)
                    .layoutManager!!
                    .onSaveInstanceState()

            nestedRecyclerViews.remove(headerPair.first)
        }
    }

    /**
     * Сохраняет состояние вложенных RecyclerView
     */
    fun saveNestedRecyclerViewsState() {
        nestedRecyclerViews.forEach {
            headersVOs!![it.key].nestedRecyclerViewParcelable = it.value.layoutManager!!.onSaveInstanceState()
        }
    }

    /**
     * Возвращает ViewHolder, внутри которого находится View с указанным типом
     *
     * @param parent Родительский элемент (обычно сам RecyclerView).
     * @param viewType Тип View, которую нужно поместить во ViewHolder
     * @return ViewHolder с нужным типом View внутри
     */
    abstract fun createItemHolder(parent: ViewGroup, viewType: Int): VH

    /**
     * Забивает данные во ViewHolder на необходимой позиции.
     * Внимание! Позиция не пересчитана, при необходимости её можно пересчитать методов recalculatePosition()
     *
     * @param holder ViewHolder, в который нужно забить данные
     * @param position Позиция элемента, в который нужно забить данные
     */
    abstract fun bindItemHolder(holder: VH, position: Int)

    /**
     * Возвращает тип View на определенной позиции
     * Не должен возвращать значения, равные -1 и -2 (может поломать футеры и шапки)
     *
     * @param position Позиция, для которой нужно определить тип View.
     * @return Тип View, предпочтительной для данной позиции
     */
    open fun getItemType(position: Int): Int = 0

    /**
     * Возвращает ViewObject шапки на определенной позиции
     *
     * @param position Позиция, на которой нужно определить шапку
     * @return Если шапка существует, то вернет её ViewObject,
     * в противном случае возвращает null
     */
    open fun getHeaderByPosition(position: Int): ViewListHeaderVO? = null

    /**
     * Возвращает тип выбранной сортировки
     *
     * @param type Тип заголовка
     * @return Тип выбранной сортировки
     */
    open fun getSortingTypeByHeader(type: Int, toggleTag: Int): Int {
        return headersVOs!![type].getSelectedFunctionalToggleTag(toggleTag)
    }

    /**
     * Выдает тип заголовка по типу элемента после него
     *
     * @param itemType Тип элемента
     * @return Тип заголовка
     */
    open fun getHeaderTypeByItemType(itemType: Int): Int = itemType

    /**
     * Определяет позицию для новой шапки нужного типа
     *
     * @param type Тип шапки
     * @return Позиция новой шапки
     */
    open fun getPositionForNewHeader(type: Int): Int = 0

    /**
     * Возвращает текст футера на определенной позиции
     * @param position Позиция, на которой нужно определить футер
     * @return Если футер существует, то вернет строку с её названием,
     * в противном случае возвращает null
     */
    open fun getFooterText(position: Int): String? = null

    /**
     * Возвращает отступ текущего элемента относительно предыдущего.
     *
     * @param position Позиция элемента, для которого нужно определить отступ
     * @return Возвращает отступ в пикселях
     */
    open fun getItemMargin(position: Int): Int = 0

    /**
     * Возвращает количество футеров.
     * Должен вернуть именно количество активных футеров!
     *
     * @result Количество активных футеров
     */
    open fun getFooterCount(): Int = 0

    /**
     * Определяет возможность создания шапки/футера?
     */
    open fun canCreateHeaderOrFooter(): Boolean = true

    /**
     * Возвращает количество элементов после шапки
     * Учитывать футеры при подсчете не нужно!
     *
     * @param type Тип шапки
     * @result Количество элементов после шапки
     */
    abstract fun getItemsCountAfterHeader(type: Int): Int

    /**
     * Строит хеш-таблицу с позициями и буквами на них
     *
     * @return Хеш-таблица вида позиция-буква (null если
     * не нужно использовать "липкие" буквы)
     */
    open fun buildStickyLettersMap(): Map<Int, String>? = null

    /**
     * Определяет тип способа добавления отступа
     *
     * @result Вернет true если отступ нужно добавить с помощью декоратора,
     * false если с помощью внутренних отступов.
     */
    open fun canCreateMarginViaDecorators(): Boolean = false

    /**
     * Возвращает количество шапок.
     * Должен вернуть именно количество активных шапок.!
     *
     * @result Количество активных шапок.
     */
    open fun getHeadersCount(): Int = 0

    private class HeaderViewHolder(
            val view: ViewListHeaderView
    ) : RecyclerView.ViewHolder(view)

    private class FooterViewHolder(
            val view: AppCompatTextView
    ) : RecyclerView.ViewHolder(view)

    private class LoaderViewHolder(
            val view: ProgressBar
    ) : RecyclerView.ViewHolder(view)
}