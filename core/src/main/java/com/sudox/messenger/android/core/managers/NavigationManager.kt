package com.sudox.messenger.android.core.managers

interface NavigationManager {

    /**
     * Выполняет действие навигации.
     * P.S.: Реализация просто переводит данный ID в ID гугловской навигации,
     * дабы обеспечить её работу в многомодульном проекте.
     *
     * @param id ID фрагмента
     */
    fun doAction(id: Int)
}