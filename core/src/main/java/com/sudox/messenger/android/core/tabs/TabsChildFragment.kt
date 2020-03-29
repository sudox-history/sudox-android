package com.sudox.messenger.android.core.tabs

import android.content.Context
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment

/**
 * Дочерний фрагмент фрагмента с ViewPager'ом и табами
 */
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
     *
     * @return True если AppBar настраивается корневым фрагментом,
     * False если нет, необходимо настроить AppBar в данном фрагменте.
     */
    fun isAppBarConfiguredByRoot(): Boolean {
        return false
    }

    /**
     * Возвращает название данной вкладки.
     *
     * @param context Контекст приложения/активности
     * @return Заголовок данного фрагмента в TabLayout'е
     */
    fun getTitle(context: Context): String
}