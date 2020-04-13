package ru.sudox.android.core.managers

import android.os.Bundle
import ru.sudox.android.core.CoreFragment

@Deprecated("will be removed")
interface NavigationManager {

    /**
     * Включает отображение части авторизации
     */
    fun showAuthPart()

    /**
     * Включает отображение основной части
     */
    fun showMainPart()

    /**
     * Настраивает навигационный бар (по тиму BottomNavigationView)
     * Не использовать из дочерних модулей!
     */
    fun configureNavigationBar()

    /**
     * Переключает на дочерний фрагмент.
     *
     * @param fragment Фрагмент, который нужно отобразить как дочерний.
     */
    fun showChildFragment(fragment: CoreFragment)

    /**
     * Производит откат на предыдущий фрагмент
     *
     * @return True если откат был произведен,
     * False если больше нет фрагментов.
     */
    fun popBackstack(): Boolean

    /**
     * Восстанавливает состояние фрагментов из Bundle
     *
     * @param bundle Bundle с состояниями фрагментом.
     * @return True если было произведено восстановление,
     * False если нечего восстанавливать.
     */
    fun restoreState(bundle: Bundle): Boolean

    /**
     * Сохраняет состояние фрагментов в Bundle.
     *
     * @param bundle Bundle, в который нужно сохранить состояния.
     */
    fun saveState(bundle: Bundle)
}