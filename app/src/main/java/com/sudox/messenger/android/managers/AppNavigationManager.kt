package com.sudox.messenger.android.managers

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.sudox.messenger.android.R
import com.sudox.messenger.android.auth.FROM_AUTH_CODE_TO_REGISTER_ACTION_ID
import com.sudox.messenger.android.auth.FROM_AUTH_PHONE_TO_CODE_ACTION_ID
import com.sudox.messenger.android.auth.FROM_AUTH_PHONE_TO_COUNTRIES_ACTION_ID
import com.sudox.messenger.android.core.managers.NavigationManager

class AppNavigationManager : NavigationManager {

    private val actionsTable = hashMapOf(
            FROM_AUTH_PHONE_TO_CODE_ACTION_ID to R.id.action_authPhoneFragment_to_authCodeFragment,
            FROM_AUTH_PHONE_TO_COUNTRIES_ACTION_ID to R.id.action_authPhoneFragment_to_authCountryFragment,
            FROM_AUTH_CODE_TO_REGISTER_ACTION_ID to R.id.action_authCodeFragment_to_authRegisterFragment
    )

    override fun doAction(navController: NavController, id: Int) {
        val options = NavOptions.Builder()
                .setEnterAnim(R.animator.animator_fragment_enter_right)
                .setExitAnim(R.animator.animator_fragment_exit_right)
                .setPopEnterAnim(R.animator.animator_fragment_enter_left)
                .setPopExitAnim(R.animator.animator_fragment_exit_left)
                .build()

        navController.navigate(actionsTable[id]!!, null, options)
    }

    override fun <T> receiveData(navController: NavController, key: String, lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        navController
                .currentBackStackEntry!!.savedStateHandle
                .getLiveData<T>(key)
                .observe(lifecycleOwner, observer)
    }

    override fun <T> sendData(navController: NavController, key: String, data: T) {
        navController
                .previousBackStackEntry!!
                .savedStateHandle
                .getLiveData<T>(key)
                .postValue(data)
    }
}