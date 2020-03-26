package com.sudox.design.appbar.vos

import android.content.Context
import android.view.View

interface AppBarLayoutVO {

    /**
     * Возвращает View, которые должны быть отображены после AppBar'а
     *
     * @param context Контекст приложения/активности
     * @return View, отображаемые после AppBar'а
     */
    fun getViews(context: Context): Array<View>?
}