package ru.sudox.android.core.managers

import ru.sudox.design.appbar.vos.AppBarLayoutVO
import ru.sudox.design.appbar.vos.AppBarVO

interface AppBarManager {

    /**
     * Переключает менеджер в режим сохранения VO.
     * В таком режиме будет сохранять только последнюю VO,
     * которая должна быть установлена после отключения данного режима.
     *
     * @param toggle True - включить режим, False - выключить режим
     */
    fun onlyStoreChanges(toggle: Boolean)

    /**
     * Выставляет ViewObject AppBar'у
     * Если передать null, то скроет AppBar
     *
     * @param vo ViewObject AppBar'а.
     * @param callback Функция для обратного вызова.
     * @param force Игнорировать проверку
     */
    fun setVO(vo: AppBarVO?, callback: ((Int) -> (Unit))?, force: Boolean = false)

    /**
     * Выставляет ViewObject AppBarLayout'у
     * Если передать null, то скроет AppBarLayout
     *
     * @param vo ViewObject AppBarLayout'а.
     */
    fun setLayoutVO(vo: AppBarLayoutVO?)

    /**
     * Запрашивает изменение режима отображения тени у AppBarLayout'а
     * Не гарантирует, что тень будет включена или выключена.
     *
     * @param toggle Включить или выключить тень?
     * @param animate Показать анимацию переключения?
     */
    fun requestElevationToggling(toggle: Boolean, animate: Boolean)

    /**
     * Выдает количество дочерних элементов AppBarLayout'а
     * В количество входит и сам AppBar
     *
     * @return Количество дочерних элементов
     */
    fun getLayoutChildCount(): Int

    /**
     * Переключает загрузку.
     * Блокирует кнопки справа
     *
     * @param toggle Включить загрузку?
     */
    fun toggleLoading(toggle: Boolean)

    /**
     * Вызывается при создании Activity
     */
    fun onStart()

    /**
     * Вызывается при остановке Activity
     */
    fun onStop()
}