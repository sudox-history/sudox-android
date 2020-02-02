package com.sudox.design.viewlist

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView

private const val HEADER_VIEW_TYPE = -1
private const val FOOTER_VIEW_TYPE = -2

abstract class ViewListAdapter<VH : RecyclerView.ViewHolder>(
        private val viewList: ViewList
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HEADER_VIEW_TYPE && getHeadersCount() > 0) {
            ViewHolder(AppCompatTextView(viewList.context).apply {
                TextViewCompat.setTextAppearance(this, viewList.headerTextAppearance)
            })
        } else if (viewType == FOOTER_VIEW_TYPE && getFooterCount() > 0) {
            ViewHolder(AppCompatTextView(viewList.context).apply {
                TextViewCompat.setTextAppearance(this, viewList.footerTextAppearance)
            })
        } else {
            createItemHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val header = getHeaderText(position)

            if (header != null) {
                holder.view.text = header
                return
            }

            val footer = getFooterText(position)

            if (footer != null) {
                holder.view.text = footer
                return
            }
        }

        bindItemHolder(holder, position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (getHeaderText(position) != null) {
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
            if (getHeaderText(i) != null || getFooterText(i) != null) {
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
     * Возвращает название шапки на определенной позиции
     *
     * @param position Позиция, на которой нужно определить шапку
     * @return Если шапка существует, то вернет строку с её названием,
     * в противном случае возвращает null
     */
    open fun getHeaderText(position: Int): String? = null

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
     * Возвращает количество активных элементов.
     * Должен вернуиь именно количество активных элементов!
     *
     * @result Количество активных элементов
     */
    open fun getItemsCount(): Int = 0

    private class ViewHolder(
            val view: AppCompatTextView
    ) : RecyclerView.ViewHolder(view)
}