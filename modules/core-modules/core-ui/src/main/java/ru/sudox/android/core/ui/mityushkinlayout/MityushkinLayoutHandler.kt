package ru.sudox.android.core.ui.mityushkinlayout

import android.util.Size
import androidx.recyclerview.widget.GridLayoutManager

abstract class MityushkinLayoutHandler : GridLayoutManager.SpanSizeLookup() {

    /**
     * Выдает количество ячеек на одной линии
     *
     * @return Количество ячеек.
     */
    abstract fun getCellsCount(): Int

    /**
     * Выдает максимальный размер View
     *
     * @param position Позиция View
     * @return Максимальный размер
     */
    open fun getMaximumSize(position: Int): Size? = null

    /**
     * Выдает минимальный размер View
     *
     * @param position Позиция View
     * @return Минимальный размер
     */
    open fun getMinimumSize(position: Int): Size? = null
}