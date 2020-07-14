package ru.sudox.android.people.impl

import androidx.fragment.app.Fragment
import ru.sudox.android.people.api.PeopleFeatureApi

class PeopleFeatureImpl : PeopleFeatureApi {
    override fun getContainerFragment(): Fragment = PeopleFlowFragment()
}