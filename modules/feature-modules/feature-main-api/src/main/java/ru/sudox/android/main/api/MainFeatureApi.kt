package ru.sudox.android.main.api

import androidx.fragment.app.Fragment

interface MainFeatureApi {

    /**
     * Выдает фрагмент-контейнер основной части приложения.
     */
    fun getStartupFragment(): Fragment
}