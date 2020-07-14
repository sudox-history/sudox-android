package ru.sudox.android.people.api

import androidx.fragment.app.Fragment

interface PeopleFeatureApi {

    /**
     * Возвращает фрагмент функционала People.
     *
     * @return Фрагмент-контейнер.
     */
    fun getContainerFragment(): Fragment
}