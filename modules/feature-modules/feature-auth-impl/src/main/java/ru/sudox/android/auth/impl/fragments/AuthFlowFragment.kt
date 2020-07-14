package ru.sudox.android.auth.impl.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import ru.sudox.android.auth.impl.R
import ru.sudox.android.auth.impl.fragments.phone.AuthPhoneFragment

/**
 * Фрагмент-контейнер авторизации.
 */
@AndroidEntryPoint
class AuthFlowFragment : Fragment(R.layout.fragment_auth_flow) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (childFragmentManager.fragments.isEmpty()) {
            childFragmentManager.commit {
                add(R.id.authFlowContainer, AuthPhoneFragment())
            }
        }
    }
}