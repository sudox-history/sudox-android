package ru.sudox.android.auth.api

import androidx.fragment.app.Fragment

/**
 * API модуля функционала авторизации.
 */
interface AuthFeatureApi {

    /**
     * Выдает стартовый фрагмент функционала авторизации
     *
     * @return Фрагмент-контейнер.
     */
    fun getStartupFragment(): Fragment
}