package ru.sudox.android.dialogs.api

import androidx.fragment.app.Fragment

/**
 * API функционала модуля диалогов
 */
interface DialogsFeatureApi {

    /**
     * Выдает стартовый фрагмент функционала диалогов.
     *
     * @return Фрагмент-контейнер.
     */
    fun getContainerFragment(): Fragment
}