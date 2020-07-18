package ru.sudox.android.core.ui.navigation

interface ContainerFragment {

    /**
     * Вызывается при нажатии кнопки "назад"
     * Должен закрыть последний фрагмент.
     *
     * @return Было ли совершенно какое-нибудь действие?
     */
    fun onBackPressed(): Boolean = false
}