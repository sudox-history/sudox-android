package ru.sudox.android.auth.impl

import androidx.fragment.app.Fragment
import ru.sudox.android.auth.api.AuthFeatureApi
import ru.sudox.android.auth.impl.fragments.AuthFlowFragment

/**
 * Реализация функционала модуля авторизации.
 */
class AuthFeatureImpl : AuthFeatureApi {
    override fun getStartupFragment(): Fragment = AuthFlowFragment()
}