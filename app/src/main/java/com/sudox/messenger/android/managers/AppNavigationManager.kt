package com.sudox.messenger.android.managers

import androidx.navigation.NavController
import com.sudox.messenger.android.R
import com.sudox.messenger.android.auth.FROM_AUTH_CODE_TO_REGISTER_ACTION_ID
import com.sudox.messenger.android.auth.FROM_AUTH_PHONE_TO_CODE_ACTION_ID
import com.sudox.messenger.android.auth.FROM_AUTH_PHONE_TO_COUNTRIES_ACTION_ID
import com.sudox.messenger.android.core.managers.NavigationManager

class AppNavigationManager(
        navControllerLazy: Lazy<NavController>
) : NavigationManager {

    private val navController by navControllerLazy
    private val actionsTable = hashMapOf(
            FROM_AUTH_PHONE_TO_CODE_ACTION_ID to R.id.action_authPhoneFragment_to_authCodeFragment,
            FROM_AUTH_PHONE_TO_COUNTRIES_ACTION_ID to R.id.action_authPhoneFragment_to_authCountryFragment,
            FROM_AUTH_CODE_TO_REGISTER_ACTION_ID to R.id.action_authCodeFragment_to_authRegisterFragment
    )

    override fun doAction(id: Int) {
        navController.navigate(actionsTable[id]!!)
    }
}