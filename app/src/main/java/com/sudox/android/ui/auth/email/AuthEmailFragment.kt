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
import com.sudox.protocol.models.enums.ConnectionState
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.fragment_auth_email.*
import kotlinx.android.synthetic.main.fragment_main_contacts.*
import kotlinx.android.synthetic.main.view_navigation_bar.view.*
import javax.inject.Inject

class AuthEmailFragment @Inject constructor() : FreezableFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var authEmailViewModel: AuthEmailViewModel
    lateinit var authActivity: AuthActivity

    // Observer
    private var connectionObserver = Observer<ConnectionState> {
        if (it == ConnectionState.CONNECTION_CLOSED) {
            showWaitForConnectStatus()
        } else if (it == ConnectionState.HANDSHAKE_SUCCEED) {
            initNavigationBar()
        }
    }

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
            emailEditTextContainer.error = when (it) {
                Errors.INVALID_PARAMETERS -> getString(R.string.wrong_email_format)
                Errors.TOO_MANY_ATTEMPTS -> getString(R.string.exceeded_limit_of_attempts)
                else -> getString(R.string.unknown_error)
            }

            unfreeze()
        })

        authEmailViewModel.protocolClient.connectionStateLiveData.observeForever(connectionObserver)
        authEmailViewModel.authEmailActionLiveData.observe(this, Observer {
            if (it == AuthEmailAction.FREEZE) {
                freeze()
            }
        })


        // Init layout components
        initEmailEditText()
        initNavigationBar()
    }

    override fun onResume() {
        super.onResume()

        if (!authEmailViewModel.protocolClient.isValid()) {
            showWaitForConnectStatus()
        }
    }

    private fun showWaitForConnectStatus() {
        authActivity.authNavigationBar.setClickable(authActivity.authNavigationBar.buttonNavbarNext, false)
        authActivity.authNavigationBar.buttonNavbarNext.setCompoundDrawables(null, null, null, null)
        authActivity.authNavigationBar.buttonNavbarNext.text = getString(R.string.wait_for_connect)
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
                emailEditTextContainer.error = null

                // Запросим отправку кода у сервера (ошибки прилетят в LiveData)
                authEmailViewModel.requestCode(emailEditText.text.toString())
            }
        }

        authActivity.authNavigationBar.configureComponents()
        authActivity.authNavigationBar.setClickable(authActivity.authNavigationBar.buttonNavbarNext, true)
        authActivity.authNavigationBar.buttonNavbarNext.text = authActivity.authNavigationBar.nextButtonText
        authActivity.authNavigationBar.buttonNavbarNext.setCompoundDrawables(null, null, authActivity.authNavigationBar.originalRightDrawableNext, null)
    }

    override fun onDetach() {
        authEmailViewModel.protocolClient.connectionStateLiveData.removeObserver(connectionObserver)

        // Super!
        super.onDetach()
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