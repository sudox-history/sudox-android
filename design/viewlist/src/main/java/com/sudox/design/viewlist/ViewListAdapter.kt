package com.sudox.design.viewlist

import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView

private const val HEADER_VIEW_TYPE = -1
private const val FOOTER_VIEW_TYPE = -2

abstract class ViewListAdapter<VH : RecyclerView.ViewHolder>(
        private val viewList: ViewList
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var initialTopPadding = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HEADER_VIEW_TYPE && getHeadersCount() > 0) {
            ViewHolder(AppCompatTextView(ContextThemeWrapper(viewList.context, viewList.headerTextAppearance)))
        } else if (viewType == FOOTER_VIEW_TYPE && getFooterCount() > 0) {
            ViewHolder(AppCompatTextView(ContextThemeWrapper(viewList.context, viewList.footerTextAppearance)))
        } else {
            createItemHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemView is ViewGroup) {
            (holder.itemView as ViewGroup).clipToPadding = false
        }

        holder.itemView.updatePadding(
                left = viewList.initialPaddingLeft,
                right = viewList.initialPaddingRight
        )

        if (holder is ViewHolder) {
            val header = getHeaderTextByPosition(position)

            if (header != null) {
                holder.view.text = header

                if (position == 0 && initialTopPadding == -1) {
                    initialTopPadding = holder.view.paddingTop
                    holder.view.updatePadding(top = 0)
                }

                return
            }

            val footer = getFooterText(position)

            if (footer != null) {
                holder.view.text = footer
                return
            }
        }

        val itemMargin = getItemMargin(position)
        val itemPaddingTop = itemMargin / 2
        val itemPaddingBottom = itemMargin / 2

        bindItemHolder(holder, position).apply {
            holder.itemView.updatePadding(
                    top = itemPaddingTop,
                    bottom = itemPaddingBottom
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getHeaderTextByPosition(position) != null) {
            HEADER_VIEW_TYPE
        } else if (getFooterText(position) != null) {
            FOOTER_VIEW_TYPE
        } else {
            getItemType(position)
        }
    }

    override fun getItemCount(): Int {
        return getHeadersCount() + getItemsCount() + getFooterCount()
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
        var headerPosition = findHeaderPosition(type)
        val itemPosition = position + 1

        if (headerPosition == -1) {
            headerPosition = getPositionForNewHeader(type, itemCount)
            notifyItemInserted(headerPosition)
        }

        notifyItemRangeInserted(itemPosition + headerPosition, itemCount)
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
        val headerPosition = findHeaderPosition(type)
        var startPosition = headerPosition + position + 1

        if (getItemsCountAfterHeader(type) == 0) {
            notifyItemRemoved(headerPosition)
            startPosition--
        }

        notifyItemRangeRemoved(startPosition, itemCount)
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
        val headerPosition = findHeaderPosition(type)
        val startPosition = headerPosition + position + 1

        notifyItemRangeChanged(startPosition, count)
    }

    /**
     * Уведомляет RecyclerView о перемещении элемента.
     *
     * @param type Тип шапки
     * @param fromPosition Позиция, относительно шапки, с которой переместился элемент
     * @param toPosition Позиция, относительно шапки, на которую переместился элемент
     */
    fun notifyItemMovedAfterHeader(type: Int, fromPosition: Int, toPosition: Int) {
        // Cannot be -1, because item created and consequently header also created
        val headerPosition = findHeaderPosition(type)
        val itemFromPosition = headerPosition + fromPosition + 1
        val itemToPosition = headerPosition + toPosition + 1

        notifyItemMoved(itemFromPosition, itemToPosition)
    }

    /**
     * Ищет позицию шапки, на которой находится указанный текст
     *
     * @param type Тип заголовка для поиска
     * @result Позиция найденной View, -1 если View не найдена ...
     */
    fun findHeaderPosition(type: Int): Int {
        val needText = getHeaderTextByType(type)

        for (i in 0 until viewList.childCount) {
            val child = viewList.getChildAt(i)
            val holder = viewList.getChildViewHolder(child)

            if (holder is ViewHolder && holder.view.text == needText) {
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
            if (getHeaderTextByPosition(i) != null || getFooterText(i) != null) {
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
    abstract fun bindItemHolder(holder: RecyclerView.ViewHolder, position: Int)

    /**
     * Возвращает тип View на определенной позиции
     * Не должен возвращать значения, равные -1 и -2 (может поломать футеры и шапки)
     *
     * @param position Позиция, для которой нужно определить тип View.
     * @return Тип View, предпочтительной для данной позиции
     */
    open fun getItemType(position: Int): Int = 0

    /**
     * Возвращает текст шапки по его типу
     *
     * @param type Тип шапки
     * @return Текст шапки
     */
    open fun getHeaderTextByType(type: Int): String? = null

    /**
     * Возвращает название шапки на определенной позиции
     *
     * @param position Позиция, на которой нужно определить шапку
     * @return Если шапка существует, то вернет строку с её названием,
     * в противном случае возвращает null
     */
    open fun getHeaderTextByPosition(position: Int): String? = null

    /**
     * Определяет позицию для новой шапки нужного типа
     *
     * @param type Тип шапки
     * @param itemCount Количество элементов, которые будут после шапки
     * @return Позиция новой шапки
     */
    open fun getPositionForNewHeader(type: Int, itemCount: Int): Int = 0

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
     * Возвращает количество шапок.
     * Должен вернуть именно количество активных шапок.!
     *
     * @result Количество активных шапок.
     */
    open fun getHeadersCount(): Int = 0

    /**
     * Возвращает количество футеров.
     * Должен вернуть именно количество активных футеров!
     *
     * @result Количество активных футеров
     */
    open fun getFooterCount(): Int = 0

    /**
     * Возвращает количество элементов после шапки
     * Учитывать футеры при подсчете не нужно!
     *
     * @param type Тип шапки
     * @result Количество элементов после шапки
     */
    open fun getItemsCountAfterHeader(type: Int): Int = 0

    /**
     * Возвращает количество активных элементов.
     * Должен вернуть именно количество активных элементов!
     *
     * @result Количество активных элементов
     */
    open fun getItemsCount(): Int = 0

    private class ViewHolder(
            val view: AppCompatTextView
    ) : RecyclerView.ViewHolder(view)
}