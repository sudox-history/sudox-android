package ru.sudox.android.main.impl

import androidx.fragment.app.Fragment
import ru.sudox.android.main.api.MainFeatureApi

class MainFeatureImpl : MainFeatureApi {
    override fun getStartupFragment(): Fragment = MainFlowFragment()
}