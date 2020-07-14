package ru.sudox.android.auth.impl.fragments.verify

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_auth_signup.*
import kotlinx.android.synthetic.main.fragment_auth_verify.*
import ru.sudox.android.auth.impl.R
import ru.sudox.android.core.ui.applyInserts
import ru.sudox.android.core.ui.toolbar.helpers.setupWithScrollView

/**
 * Фрагмент ожидания подтверждения авторизации
 */
class AuthVerifyFragment : Fragment(R.layout.fragment_auth_verify) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authVerifyAppBarLayout.setupWithScrollView(authVerifyScrollView)
        authVerifyScrollView.applyInserts(top = false, bottom = true)
    }
}