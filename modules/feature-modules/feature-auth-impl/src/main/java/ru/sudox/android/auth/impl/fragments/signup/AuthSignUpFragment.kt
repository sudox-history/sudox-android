package ru.sudox.android.auth.impl.fragments.signup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_auth_code.*
import kotlinx.android.synthetic.main.fragment_auth_phone.*
import kotlinx.android.synthetic.main.fragment_auth_signup.*
import ru.sudox.android.auth.impl.R
import ru.sudox.android.core.ui.applyInserts
import ru.sudox.android.core.ui.setupWithFragmentManager
import ru.sudox.android.core.ui.toolbar.helpers.setupWithScrollView

/**
 * Фрагмент регистрации пользователя
 */
class AuthSignUpFragment : Fragment(R.layout.fragment_auth_signup) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authSignupScrollView.applyInserts(top = false, bottom = true)
        authSignupAppBarLayout.setupWithScrollView(authSignupScrollView)
        authSignupToolbar.setupWithFragmentManager(requireActivity(), parentFragmentManager)
    }
}