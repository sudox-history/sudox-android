package com.sudox.android.ui.auth.email

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.data.models.Errors
import com.sudox.android.ui.auth.AuthActivity
import com.sudox.android.ui.auth.email.enums.AuthEmailAction
import com.sudox.android.ui.common.FreezableFragment
import com.sudox.design.navigation.toolbar.enums.NavigationAction
import kotlinx.android.synthetic.main.activity_auth.*
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
        authEmailViewModel.authErrorsLiveData.observe(this, Observer {
            emailEditTextContainer.error = if (it == Errors.INVALID_PARAMETERS) {
                getString(R.string.wrong_email_format)
            } else if (it == Errors.TOO_MANY_ATTEMPTS) {
                getString(R.string.too_many_requests)
            } else {
                getString(R.string.unknown_error)
            }

            unfreeze()
        })

        authEmailViewModel.authEmailActionLiveData.observe(this, Observer {
            if (it == AuthEmailAction.FREEZE) {
                freeze()
            }
        })

        // Init layout components
        initEmailEditText()
        initNavigationBar()
    }

    private fun initEmailEditText() {
        emailEditText.setText(email)
    }

    private fun initNavigationBar() {
        authActivity.authNavigationBar.reset()
        authActivity.authNavigationBar.nextButtonIsVisible = true
        authActivity.authNavigationBar.sudoxTagIsVisible = false
        authActivity.authNavigationBar.navigationActionCallback = {
            if (it == NavigationAction.NEXT) {
                // Получим текст с EditText'а :) (да Антон, мне в кайф комментировать каждую строчку кода)
                val email = emailEditText.text.toString()

                // Запросим отправку кода у сервера (ошибки прилетят в LiveData)
                authEmailViewModel.requestCode(email)
            }
        }

        authActivity.authNavigationBar.configureComponents()
    }

    override fun freeze() {
        authActivity.authNavigationBar.freeze()
        emailEditText.isEnabled = false
    }

    override fun unfreeze() {
        authActivity.authNavigationBar.unfreeze()
        emailEditText.isEnabled = true
    }
}