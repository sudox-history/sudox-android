package com.sudox.design.quiltview.patterns

import com.sudox.design.quiltview.QuiltView

interface Pattern {

    /**
     * Производит измерение размера сетки и её элементов
     *
     * @param widthSize Максимальная доступная ширина
     * @param widthMode Режим измерения ширины
     * @param heightSize Максимальная доступная высота
     * @param heightMode Режим измерения высоты
     * @param adapter Адаптер, который поставил данный шаблон
     * @param view View, с которой связан адаптер
     *
     * @return Пара ширина-высота
     */
    fun measure(widthSize: Int, widthMode: Int, heightSize: Int, heightMode: Int, adapter: PatternAdapter, view: QuiltView): Pair<Int, Int>

    /**
     * Располагает элементы сетки
     *
     * @param left Начальная точка на оси X
     * @param top Начальная точка на оси Y
     * @param adapter Адаптер, который поставил данный шаблон
     * @param view View, с которой связан адаптер
     */
    fun layout(left: Int, top: Int, adapter: PatternAdapter, view: QuiltView)
}