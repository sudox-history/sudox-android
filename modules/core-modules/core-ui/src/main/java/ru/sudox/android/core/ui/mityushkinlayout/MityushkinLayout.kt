package ru.sudox.android.core.ui.mityushkinlayout

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Layout, располагающий View в сетке.
 */
class MityushkinLayout : RecyclerView {

    private var handler: MityushkinLayoutHandler? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * Выставляет обработчик сетки
     * Выставляет менеджер разметки и связывает его с обработчиком
     *
     * @param handler Обработчик сетки
     */
    fun setHandler(handler: MityushkinLayoutHandler) {
        this.handler = handler

        layoutManager = GridLayoutManager(context, handler.getCellsCount()).apply {
            spanSizeLookup = handler
        }
    }
}