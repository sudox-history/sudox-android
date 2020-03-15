package com.sudox.design.quiltview.patterns

import com.sudox.design.quiltview.QuiltView

/**
 * Адаптер шаблонов для построения сетки
 */
interface PatternAdapter {

    /**
     * Вызывается во время связывания адаптера с QuiltView.
     * Можно подгрузить необходимые ресурсы, т.к. контекст во View уже подгружен
     *
     * @param view QuiltView, с которым был связан адаптер
     */
    fun onAttached(view: QuiltView)

    /**
     * Выдает шаблон сетки для необходимого количества View
     *
     * @param count Количество дочерних View
     * @return Шаблон сетки (null если не был найден)
     */
    fun getPattern(count: Int): Pattern?
}