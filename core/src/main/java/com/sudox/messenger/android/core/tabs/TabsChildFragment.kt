package com.sudox.messenger.android.core.tabs

import android.content.Context
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment

interface TabsChildFragment {

    /**
     * Вызывается после того, как фрагмент будет выбран ViewPager'ом
     * Нужно подготовить фрагмент к отображению.
     *
     * @param activity Основная активность приложения
     * @param fragment Фрагмент, который нужно подготовить
     */
    fun prepareToShowing(activity: CoreActivity, fragment: CoreFragment) {
        if (!isAppBarConfiguredByRoot()) {
            fragment.apply {
                // P.S.: CoreFragment можно использовать только если Activity является наследником CoreActivity
                activity.setAppBarViewObject(appBarVO, ::onAppBarClicked)
            }
        }
    }

    /**
     * Сконфигурирован ли AppBar корневым фрагментом?
     */
    fun isAppBarConfiguredByRoot(): Boolean {
        return false
    }

    /**
     * Возвращает название данной вкладки.
     *
     * @param context Контекст приложения/активности
     */
    fun getTitle(context: Context): String
}