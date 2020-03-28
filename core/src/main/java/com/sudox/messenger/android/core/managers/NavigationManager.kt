package com.sudox.messenger.android.core.managers

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavController

interface NavigationManager {

    /**
     * Выполняет действие навигации.
     * P.S.: Реализация просто переводит данный ID в ID гугловской навигации,
     * дабы обеспечить её работу в многомодульном проекте.
     *
     * @param id ID фрагмента
     */
    fun doAction(navController: NavController, id: Int)

    /**
     * Слушает информацию из SavedStateHandle.
     * Можно использовать для получения информации с дочерного фрагмента (например: выбор страны)
     *
     * @param key Ключ канала с которого нужно слушать информацию.
     * @param observer Observer на который нужно будет передать полученную информацию.
     */
    fun <T> receiveData(navController: NavController, key: String, lifecycleOwner: LifecycleOwner, observer: Observer<T>)

    /**
     * Передает информацию в SavedStateHandle, а дальше от него к слушателям
     * Можно использовать для отправки информации с дочерного фрамента (например: выбор страны)
     *
     * @param key Ключ канала, на который нужно отправить информацию
     * @param data Информация для отправки в канал
     */
    fun <T> sendData(navController: NavController, key: String, data: T)
}