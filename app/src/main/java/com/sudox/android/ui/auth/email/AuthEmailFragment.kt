package com.sudox.android.ui.auth.email

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.design.helpers.hideInputError
import com.sudox.design.helpers.showInputError
import com.sudox.design.navigation.toolbar.enums.NavigationAction
import com.sudox.android.ui.auth.AuthActivity
import com.sudox.android.ui.auth.email.enums.AuthEmailAction
import com.sudox.android.ui.common.FreezableFragment
import kotlinx.android.synthetic.main.fragment_auth_email.*
import javax.inject.Inject

class AuthEmailFragment @Inject constructor() : FreezableFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var authEmailViewModel: AuthEmailViewModel
    lateinit var authActivity: AuthActivity

    // Some data about of state ...
    var email: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        authEmailViewModel = getViewModel(viewModelFactory)
        authActivity = activity as AuthActivity

        return inflater.inflate(R.layout.fragment_auth_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Слушаем заказанные ViewModel действия ...
        authEmailViewModel.authEmailActionLiveData.observe(this, Observer {
            if (it == AuthEmailAction.SHOW_ERROR) {
                showInputError(emailEditTextContainer)
                unfreeze()
            } else if (it == AuthEmailAction.FREEZE) {
                freeze()
            }
        })

        // Init layout components
        initEmailEditText()
        initNavigationBar()
    }

    private fun initEmailEditText() {
        emailEditText.setText(email)
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (emailEditTextContainer.isErrorEnabled) hideInputError(emailEditTextContainer)
            }
        })
    }

    private fun initNavigationBar() {
        authEmailFragmentNavbar.navigationActionCallback = {
            if (it == NavigationAction.NEXT) {
                // Получим текст с EditText'а :) (да Антон, мне в кайф комментировать каждую строчку кода)
                val email = emailEditText.text.toString()

                // Запросим отправку кода у сервера (ошибки прилетят в LiveData)
                authEmailViewModel.requestCode(email)
            }
        }
    }

    override fun freeze() {
        authEmailFragmentNavbar.freeze()
        emailEditText.isEnabled = false
    }

    override fun unfreeze() {
        authEmailFragmentNavbar.unfreeze()
        emailEditText.isEnabled = true
    }
}