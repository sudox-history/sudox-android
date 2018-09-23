package com.sudox.android.ui.auth.register

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.ui.views.enums.NavigationAction
import com.sudox.android.common.helpers.hideInputError
import com.sudox.android.common.helpers.showInputError
import com.sudox.android.common.models.auth.state.AuthSession
import com.sudox.android.common.viewmodels.ViewModelFactory
import com.sudox.android.common.viewmodels.getViewModel
import com.sudox.android.ui.auth.AuthActivity
import com.sudox.android.ui.auth.register.enums.AuthRegisterAction
import com.sudox.android.ui.common.FreezableFragment
import kotlinx.android.synthetic.main.fragment_auth_register.*
import javax.inject.Inject

class AuthRegisterFragment @Inject constructor() : FreezableFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var authRegisterViewModel: AuthRegisterViewModel
    lateinit var authActivity: AuthActivity
    lateinit var authSession: AuthSession

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        authRegisterViewModel = getViewModel(viewModelFactory)
        authActivity = activity as AuthActivity
        authSession = authActivity.authSession

        return inflater.inflate(R.layout.fragment_auth_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Слушаем заказанные ViewModel действия ...
        authRegisterViewModel.authRegisterActionLiveData.observe(this, Observer {
            when (it) {
                AuthRegisterAction.FREEZE -> freeze()
                AuthRegisterAction.SHOW_ERROR -> {
                    showInputError(nameEditTextContainer)
                    showInputError(nicknameEditTextContainer)
                    unfreeze()
                }
                AuthRegisterAction.SHOW_EMAIL_FRAGMENT_WITH_CODE_EXPIRED_ERROR -> {
                    authActivity.showAuthEmailFragment(authSession.email)
                    authActivity.showMessage(getString(R.string.code_expired))
                }
            }
        })

        // Initializing layout components
        initEditTexts()
        initNavigationBar()
    }

    private fun initEditTexts() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(chars: CharSequence, start: Int, before: Int, count: Int) {
                if (nameEditTextContainer.isErrorEnabled) hideInputError(nameEditTextContainer)
                if (nicknameEditTextContainer.isErrorEnabled) hideInputError(nicknameEditTextContainer)
            }
        }

        // Linking watchers
        nameEditText.addTextChangedListener(watcher)
        nicknameEditText.addTextChangedListener(watcher)
    }

    private fun initNavigationBar() {
        authRegisterFragmentNavbar.navigationActionCallback = {
            if (it == NavigationAction.NEXT) {
                val name = nameEditText.text.toString()
                val nickname = nicknameEditText.text.toString()

                // Запрос ...
                authRegisterViewModel.signUp(authSession.email, authSession.code!!, authSession.hash, name, nickname)
            }
        }
    }

    override fun freeze() {
        authRegisterFragmentNavbar.freeze()

        // Блокируем ввод данных
        nameEditText.isEnabled = false
        nicknameEditText.isEnabled = false
    }

    override fun unfreeze() {
        authRegisterFragmentNavbar.unfreeze()

        // Разблокируем ввод данных
        nameEditText.isEnabled = true
        nicknameEditText.isEnabled = true
    }
}