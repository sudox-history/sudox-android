package com.sudox.design.mityushkinlayout

import android.graphics.Rect

interface MityushkinLayoutTemplate {

    /**
     * Определяет границы фигур в Layout'е
     *
     * @param widthMeasureSpec Маска ширины
     * @param heightMeasureSpec Маска высоты
     * @param adapter Адаптер, связанный с Layout'ом
     * @param layout Layout, который запрашивает расположение элементов
     * @return Прямоугольники - границы дочерних фигур
     */
    fun layout(widthMeasureSpec: Int, heightMeasureSpec: Int, adapter: MityushkinLayoutAdapter, layout: MityushkinLayout): Array<Rect>
}