package ru.sudox.design.appbar.vos

import android.content.Context
import android.view.View
import ru.sudox.design.appbar.R
import ru.sudox.design.appbar.vos.others.AppBarButtonParam
import ru.sudox.design.appbar.vos.others.NOT_USED_PARAMETER

const val APPBAR_BACK_BUTTON_TAG = 1
const val APPBAR_SEARCH_BUTTON_TAG = 2

val APPBAR_BACK_BUTTON_PARAMS = arrayOf(AppBarButtonParam(APPBAR_BACK_BUTTON_TAG, R.drawable.ic_left_arrow, NOT_USED_PARAMETER))
val APPBAR_SEARCH_BUTTON_PARAMS = arrayOf(AppBarButtonParam(APPBAR_SEARCH_BUTTON_TAG, R.drawable.ic_search, NOT_USED_PARAMETER))

/**
 * ViewObject состояния AppBar'а
 */
interface AppBarVO {

    /**
     * Выдает параметры кнопок слева.
     *
     * @return Пары тег-иконка-текст (один из параметров после тега может быть равен NOT_USED_PARAMETER если он не используется)
     */
    fun getButtonsAtLeft(): Array<AppBarButtonParam>?

    /**
     * Выдает параметры кнопок справа.
     *
     * @return Пары тег-иконка-текст (один из параметров после тега может быть равен NOT_USED_PARAMETER если он не используется)
     */
    fun getButtonsAtRight(): Array<AppBarButtonParam>?

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