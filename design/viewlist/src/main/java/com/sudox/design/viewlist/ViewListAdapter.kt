package com.sudox.design.viewlist

import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.sudox.design.viewlist.header.ViewListHeaderView
import com.sudox.design.viewlist.vos.ViewListHeaderVO

const val HEADER_VIEW_TYPE = -1
const val FOOTER_VIEW_TYPE = -2
const val LOADING_VIEW_TYPE = -3

abstract class ViewListAdapter<VH : RecyclerView.ViewHolder>(
        open val headersVOs: HashMap<Int, ViewListHeaderVO>? = null,
        open val loadingStates: HashMap<Int, Boolean> = HashMap()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var viewList: ViewList? = null
        set(value) {
            field = value?.apply {
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
        }

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
                val orientation = (viewList!!.layoutManager as? LinearLayoutManager)?.orientation ?: LinearLayoutManager.VERTICAL

                if (positionStart == 0 &&
                        ((!viewList!!.canScrollVertically(-1) && orientation == LinearLayoutManager.VERTICAL) ||
                                (!viewList!!.canScrollHorizontally(-1) && orientation == LinearLayoutManager.HORIZONTAL))
                ) {
                    viewList!!.scrollToPosition(0)
                }
            }
        }

        registerAdapterDataObserver(dataObserver!!)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        if (dataObserver != null) {
            unregisterAdapterDataObserver(dataObserver!!)
            dataObserver = null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HEADER_VIEW_TYPE && getHeadersCount() > 0) {
            HeaderViewHolder(ViewListHeaderView(viewList!!.context))
        } else if (viewType == FOOTER_VIEW_TYPE && getFooterCount() > 0) {
            FooterViewHolder(AppCompatTextView(ContextThemeWrapper(viewList!!.context, viewList!!.footerTextAppearance)))
        } else if (viewType == LOADING_VIEW_TYPE) {
            LoadingViewHolder(ProgressBar(viewList!!.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
            })
        } else {
            createItemHolder(parent, viewType)
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
                        val type = getHeaderType(vo)

                        vo.isItemsHidden = !vo.isItemsHidden
                        val itemsCount = getItemsCountAfterHeader(type, true)
                        vo.isItemsHidden = !vo.isItemsHidden

                        notifyItemChanged(holder.adapterPosition)

                        if (vo.isItemsHidden) {
                            notifyItemRangeRemoved(holder.adapterPosition + 1, itemsCount)
                        } else {
                            notifyItemRangeInserted(holder.adapterPosition + 1, itemsCount)
                        }
                    }

                    it.getItemsCountBeforeChanging = { vo -> getItemsCountAfterHeader(getHeaderType(vo)) }
                    it.itemsSectionChangingCallback = { vo, itemsCountBeforeChanging ->
                        val type = getHeaderType(vo)

                        if (!vo.isItemsHidden) {
                            toggleLoadingForHeader(type, true)
                            sectionChangedCallback!!(type, itemsCountBeforeChanging, vo)
                        }
                    }

                    it.sortTypeChangingCallback = { vo, itemsCountBeforeChanging ->
                        val type = getHeaderType(vo)

                        if (!vo.isItemsHidden) {
                            toggleLoadingForHeader(type, true)
                            sortingTypeChangedCallback!!(type, itemsCountBeforeChanging, vo)
                        }
                    }

                    it.vo = vo
                }

                if (position == 0 && initialTopPadding == -1) {
                    initialTopPadding = holder.view.paddingTop
                    holder.view.updatePadding(top = 0)
                }

                if (initialBottomPadding == -1) {
                    initialBottomPadding = holder.view.paddingBottom
                }

                if (vo.isItemsHidden) {
                    holder.view.updatePadding(bottom = 0)
                } else {
                    holder.view.updatePadding(bottom = initialBottomPadding)
                }

                return
            }
        } else if (holder is LoadingViewHolder) {
            return
        }

        @Suppress("UNCHECKED_CAST")
        bindItemHolder(holder as VH, position).apply {
            if (!canCreateMarginViaDecorators(position)) {
                val itemMargin = getItemMargin(position) / 2
                val positionAfterHeader = recalculatePositionRelativeHeader(position)

                if (orientation == LinearLayoutManager.VERTICAL) {
                    if (positionAfterHeader == 0) {
                        holder.itemView.updatePadding(bottom = itemMargin)
                    } else if (positionAfterHeader == getItemsCountAfterHeader(holder.itemViewType) - 1) {
                        holder.itemView.updatePadding(top = itemMargin)
                    } else {
                        holder.itemView.updatePadding(top = itemMargin, bottom = itemMargin)
                    }
                } else {
                    if (positionAfterHeader == 0) {
                        holder.itemView.updatePadding(right = itemMargin)
                    } else if (positionAfterHeader == getItemsCountAfterHeader(holder.itemViewType) - 1) {
                        holder.itemView.updatePadding(left = itemMargin)
                    } else {
                        holder.itemView.updatePadding(left = itemMargin, right = itemMargin)
                    }
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
            val header = getHeaderByPosition(position - 1)

            if (header != null && loadingStates[getHeaderType(header)] == true) {
                LOADING_VIEW_TYPE
            } else {
                getItemType(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return getHeadersCount() + getFooterCount() + (headersVOs?.keys?.sumBy {
            if (loadingStates[it] != true) {
                getItemsCountAfterHeader(it)
            } else {
                1
            }
        } ?: getItemsCountAfterHeader(0))
    }

    /**
     * Уведомляет RecyclerView о том, что новые данные в измененной секции готовы к отображению
     *
     * @param headerType Тип заголовка
     * @param itemsCountBeforeChanging Количество предметов после заголовка до изменения
     * @param itemsCount Количество элементов после изменения
     */
    fun notifyChangedSectionDataChanged(headerType: Int, itemsCountBeforeChanging: Int, itemsCount: Int) {
        val firstItemPosition = findHeaderPosition(headerType) + 1

        toggleLoadingForHeader(headerType, false)
        notifyItemRangeRemoved(firstItemPosition, itemsCountBeforeChanging)
        notifyItemRangeInserted(firstItemPosition, itemsCount)
    }

    private fun getHeaderType(vo: ViewListHeaderVO): Int {
        return headersVOs!!.entries.find { entry -> entry.value == vo }!!.key
    }

    /**
     * Включает/отключает загрузку после заголовка
     *
     * @param type Тип шапки
     * @param toggle Включить/выключить загрузку
     */
    fun toggleLoadingForHeader(type: Int, toggle: Boolean) {
        loadingStates[type] = toggle

        val itemsAfterHeader = getItemsCountAfterHeader(type)

        if (toggle) {
            notifyItemRangeRemovedAfterHeader(type, 0, itemsAfterHeader)
        } else {
            notifyItemRemoved(findHeaderPosition(type) + 1)
            notifyItemRangeInsertedAfterHeader(type, 0, itemsAfterHeader)
        }
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
            var headerPosition = findHeaderPosition(type)
            val itemPosition = position + 1

            if (headerPosition == -1) {
                headerPosition = getPositionForNewHeader(type)
                notifyItemInserted(headerPosition)
            }

            notifyItemRangeInserted(itemPosition + headerPosition, itemCount)
        } else {
            notifyItemRangeInserted(position, itemCount)
        }
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
            val headerPosition = findHeaderPosition(type)
            var startPosition = headerPosition + position + 1

            if (getItemsCountAfterHeader(type) == 0) {
                notifyItemRemoved(headerPosition)
                startPosition--
            }

            notifyItemRangeRemoved(startPosition, itemCount)
        } else {
            notifyItemRangeRemoved(position, itemCount)
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
            val headerPosition = findHeaderPosition(type)
            val startPosition = headerPosition + position + 1

            notifyItemRangeChanged(startPosition, count)
        } else {
            notifyItemRangeChanged(position, count)
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
            val headerPosition = findHeaderPosition(type)
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
     * @result Позиция найденной View, -1 если View не найдена ...
     */
    fun findHeaderPosition(type: Int): Int {
        val need = headersVOs!![type]

        for (i in 0 until viewList!!.childCount) {
            val child = viewList!!.getChildAt(i)
            val holder = viewList!!.getChildViewHolder(child)

            if (holder is HeaderViewHolder && holder.view.vo == need) {
                return holder.adapterPosition
            }
        }

        return -1
    }

    /**
     * Пересчитывает позицию относительно адаптера в позицию, относительно элементов (т.е. исключает количество футеров и шапок,
     * предшествующих данной позиции)
     *
     * @param position Позиция для пересчета
     * @return Позиция, относительно элементов
     */
    fun recalculatePosition(position: Int): Int {
        return position - getFootersAndHeadersCountBehindPosition(position)
    }

    /**
     * Возвращает количество футеров и шапок, предшествующих данной позиции
     *
     * @param position Позиция, относительно которой нужно посчитать количество шапок и футеров
     * @return Количество шапок и футеров, относительно позиции
     */
    fun getFootersAndHeadersCountBehindPosition(position: Int): Int {
        var footersAndHeadersCount = 0

        for (i in position downTo 0) {
            if (getHeaderByPosition(i) != null || getFooterText(i) != null) {
                footersAndHeadersCount++
            }
        }

        return footersAndHeadersCount
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

        var lastHeaderPosition = 0

        for (i in position downTo 0) {
            if (getItemViewType(i) == HEADER_VIEW_TYPE) {
                lastHeaderPosition = i
                break
            }
        }

        return position - lastHeaderPosition - 1
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
     * @param ignoreHidden Игнорировать тот факт, что секция скрыта
     * @result Количество элементов после шапки
     */
    open fun getItemsCountAfterHeader(type: Int, ignoreHidden: Boolean = false): Int = 0

    /**
     * Определяет тип способа добавления отступа
     *
     * @property position Позиция элемента
     * @result Вернет true если отступ нужно добавить с помощью декоратора,
     * false если с помощью внутренних отступов.
     */
    open fun canCreateMarginViaDecorators(position: Int): Boolean = false

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

    private class LoadingViewHolder(
            val view: ProgressBar
    ) : RecyclerView.ViewHolder(view)
}