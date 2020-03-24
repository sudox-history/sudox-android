package com.sudox.design.appbar

import android.content.Context
import android.view.View

const val NOT_USED_PARAMETER = 0

/**
 * ViewObject состояния AppBar'а
 */
interface AppBarVO {

    /**
     * Выдает параметры кнопок слева.
     *
     * @return Пары тег-иконка-текст (один из параметров после тега может быть равен NOT_USED_PARAMETER если он не используется)
     */
    fun getButtonsAtLeft(): Array<Triple<Int, Int, Int>>?

    /**
     * Выдает параметры кнопок справа.
     *
     * @return Пары тег-иконка-текст (один из параметров после тега может быть равен NOT_USED_PARAMETER если он не используется)
     */
    fun getButtonsAtRight(): Array<Triple<Int, Int, Int>>?

    /**
     * Возвращает View слева.
     *
     * @param context Контекст приложения/активности
     * @return View обьекта слева
     */
    fun getViewAtLeft(context: Context): View?

    /**
     * Возвращает View справа.
     *
     * @param context Контекст приложения/активности
     * @return View обьекта справа
     */
    fun getViewAtRight(context: Context): View?

    /**
     * Возвращает ID заголовка.
     *
     * @return ID текста заголовка (NOT_USED_PARAMETER если заголовок не нужен)
     */
    fun getTitle(): Int
}