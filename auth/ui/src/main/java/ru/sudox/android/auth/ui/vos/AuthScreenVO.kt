package ru.sudox.android.auth.ui.vos

import android.content.Context
import android.view.View

interface AuthScreenVO {

    /**
     * Выдает заголовок экрана
     *
     * @param context Контекст приложения/активности
     * @return Строка с заголовком экрана
     */
    fun getTitle(context: Context): String

    /**
     * Выдает иконку, оттенок иконки и описание экрана.
     *
     * @param context Контекст приложения/активности
     * @return Пара вида ID-иконки-ID оттенка-описание
     */
    fun getDescription(context: Context): Triple<Int, Int, CharSequence>

    /**
     * Выдает дочерние элементы экрана
     *
     * @param context Контекст приложения/активности
     * @return Массив с дочерними элементами экрана
     */
    fun getChildViews(context: Context): Array<View>
}