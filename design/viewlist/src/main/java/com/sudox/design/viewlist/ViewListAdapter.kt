package com.sudox.design.viewlist

import android.util.SparseArray
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.util.forEach
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.sudox.design.viewlist.header.ViewListHeaderView
import com.sudox.design.viewlist.vos.ViewListHeaderVO

const val HEADER_VIEW_TYPE = -1
const val FOOTER_VIEW_TYPE = -2
const val LOADER_VIEW_TYPE = -3

abstract class ViewListAdapter<VH : RecyclerView.ViewHolder>(
        open val headersVOs: SparseArray<ViewListHeaderVO>? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    open var viewList: ViewList? = null
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
            HeaderViewHolder(ViewListHeaderView(viewList!!.context).apply {
                syncWithViewList(viewList!!)
            })
        } else if (viewType == FOOTER_VIEW_TYPE && getFooterCount() > 0) {
            FooterViewHolder(AppCompatTextView(ContextThemeWrapper(viewList!!.context, viewList!!.footerTextAppearance)))
        } else if (viewType == LOADER_VIEW_TYPE) {
            LoaderViewHolder(ProgressBar(viewList!!.context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
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
                        vo.isItemsHidden = !vo.isItemsHidden
                        val itemsCount = getItemsCountAfterHeaderConsiderVisibility(vo.type, true)
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
        } else if (holder is LoaderViewHolder) {
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
                    } else if (positionAfterHeader == getItemsCountAfterHeaderConsiderVisibility(holder.itemViewType) - 1) {
                        holder.itemView.updatePadding(top = itemMargin)
                    } else {
                        holder.itemView.updatePadding(top = itemMargin, bottom = itemMargin)
                    }
                } else {
                    if (positionAfterHeader == 0) {
                        holder.itemView.updatePadding(right = itemMargin)
                    } else if (positionAfterHeader == getItemsCountAfterHeaderConsiderVisibility(holder.itemViewType) - 1) {
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
            val nearbyHeader = getNearestHeader(position) ?: return getItemType(position)
            val loaderPosition = findHeaderPosition(nearbyHeader.type) +
                    getItemsCountAfterHeaderConsiderVisibility(nearbyHeader.type) + 1

            if (loaderPosition == position) {
                LOADER_VIEW_TYPE
            } else {
                getItemType(position)
            }
        }
    }

    override fun getItemCount(): Int {
        if (headersVOs != null) {
            var itemsCount = 0

            headersVOs?.forEach { key, value ->
                itemsCount += getItemsCountAfterHeaderConsiderVisibility(key) + if (value.isContentLoading) {
                    1
                } else {
                    0
                }
            }

            return itemsCount
        } else {
            return getItemsCountAfterHeaderConsiderVisibility(0)
        }
    }

    private fun getNearestHeader(position: Int): ViewListHeaderVO? {
        for (i in position - 1 downTo 0) {
            val header = getHeaderByPosition(i)

            if (header != null) {
                return header
            }
        }

        return null
    }

    /**
     * Добавляет VO шапки в хеш-таблицу
     *
     * @param vo VO шапки, которую нужно добавить
     */
    fun addHeaderVO(vo: ViewListHeaderVO) {
        headersVOs?.append(vo.type, vo)
    }

    /**
     * Считает количество элементов, исключая скрытые если это требуется
     *
     * @param type Тип шапки, после которой нужно получить количество элементов
     * @param ignoreHidden Учитывать скрытые элементы?
     * @return Количество элементов
     */
    fun getItemsCountAfterHeaderConsiderVisibility(type: Int, ignoreHidden: Boolean = false): Int {
        if (ignoreHidden || headersVOs == null || headersVOs!![type]?.isItemsHidden == false) {
            return getItemsCountAfterHeader(type)
        }

        return 0
    }

    /**
     * Уведомляет RecyclerView о том, что новые данные в измененной секции готовы к отображению
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
        if ((headersVOs?.size() ?: 0) > 0) {
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

        if ((headersVOs?.size() ?: 0) > 0) {
            val headerPosition = findHeaderPosition(type)
            var startPosition = headerPosition + position + 1
            val vo = headersVOs!![type]

            if (vo!!.getToggleOptions(viewList!!.context).size == 1 && getItemsCountAfterHeaderConsiderVisibility(type) == 0) {
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

        if ((headersVOs?.size() ?: 0) > 0) {
            val headerPosition = findHeaderPosition(type)
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
     */
    fun toggleLoading(type: Int, toggle: Boolean) {
        if (headersVOs!![type]!!.isContentLoading == toggle) {
            return
        }

        headersVOs!![type]!!.isContentLoading = toggle

        val loaderPosition = findHeaderPosition(type) + getItemsCountAfterHeaderConsiderVisibility(type) + 1

        if (toggle) {
            notifyItemInserted(loaderPosition)
        } else {
            notifyItemRemoved(loaderPosition)
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
        if ((headersVOs?.size() ?: 0) > 0) {
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

        for (i in 0 until itemCount) {
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

        var lastHeaderPosition = 0
        var notCalculable = 0

        for (i in position downTo 0) {
            val type = getItemViewType(i)

            if (type == LOADER_VIEW_TYPE || type == FOOTER_VIEW_TYPE) {
                notCalculable++
            } else if (type == HEADER_VIEW_TYPE) {
                lastHeaderPosition = i
                break
            }
        }

        return position - notCalculable - lastHeaderPosition - 1
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
     * @result Количество элементов после шапки
     */
    open fun getItemsCountAfterHeader(type: Int): Int = 0

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

    private class LoaderViewHolder(
            val view: ProgressBar
    ) : RecyclerView.ViewHolder(view)
}