package ru.sudox.android.auth.impl.fragments.code

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import kotlinx.android.synthetic.main.fragment_auth_code.*
import kotlinx.android.synthetic.main.fragment_auth_phone.*
import ru.sudox.android.auth.impl.R
import ru.sudox.android.auth.impl.fragments.signup.AuthSignUpFragment
import ru.sudox.android.core.ui.applyInserts
import ru.sudox.android.core.ui.setChildEnterAnimations
import ru.sudox.android.core.ui.setupWithFragmentManager
import ru.sudox.android.core.ui.toolbar.helpers.setupWithScrollView

/**
 * Фрагмент ввода кода подтверждения
 */
class AuthCodeFragment : Fragment(R.layout.fragment_auth_code) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authCodeScrollView.applyInserts(top = false, bottom = true)
        authCodeAppBarLayout.setupWithScrollView(authCodeScrollView)
        authCodeToolbar.setupWithFragmentManager(requireActivity(), parentFragmentManager)
        authCodeEditText.blocksFilledCallback = {
            parentFragmentManager.commit {
                addToBackStack(null)
                setChildEnterAnimations()
                replace(R.id.authFlowContainer, AuthSignUpFragment())
            }
        }
    }
}