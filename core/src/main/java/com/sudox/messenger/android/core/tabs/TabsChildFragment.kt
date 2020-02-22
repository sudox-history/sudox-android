package com.sudox.messenger.android.core.tabs

import android.content.Context
import com.sudox.messenger.android.core.CoreFragment

interface TabsChildFragment {

    /**
     * Возвращает название данной вкладки.
     * Если название не нужно отображать, то вернуть null.
     *
     * @param context Контекст приложения/активности
     */
    fun getTitle(context: Context): String?

    /**
     * Вызывается после того, как фрагмент будет выбран ViewPager'ом
     * Нужно подготовить фрагмент к отображению.
     */
    fun prepareToShowing(coreFragment: CoreFragment) {
        coreFragment.applicationBarManager!!.reset(false)
    }
}