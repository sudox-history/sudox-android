package com.sudox.android.ui.auth.phone

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.redmadrobot.inputmask.helper.Mask
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.data.models.Errors
import com.sudox.android.ui.auth.AuthActivity
import com.sudox.android.ui.auth.common.BaseAuthFragment
import com.sudox.android.ui.auth.phone.enums.AuthEmailAction
import com.sudox.design.navigation.toolbar.enums.NavigationAction
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.fragment_auth_phone.*
import javax.inject.Inject

class AuthPhoneFragment @Inject constructor() : BaseAuthFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var authPhoneViewModel: AuthPhoneViewModel
    lateinit var authActivity: AuthActivity

    // Some data about of state ...
    var phoneNumber: String? = null
    var error: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        authPhoneViewModel = getViewModel(viewModelFactory)
        authActivity = activity as AuthActivity

        return inflater.inflate(R.layout.fragment_auth_phone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show started error
        if (error != null) phoneEditTextContainer.error = error

        // Слушаем заказанные ViewModel действия ...
        authPhoneViewModel.authErrorsLiveData.observe(this, Observer {
            phoneEditTextContainer.error = when (it) {
                Errors.INVALID_PARAMETERS -> getString(R.string.wrong_phone_format)
                Errors.TOO_MANY_REQUESTS -> getString(R.string.too_many_requests)
                else -> getString(R.string.unknown_error)
            }

            unfreeze()
        })

        authPhoneViewModel.authEmailActionLiveData.observe(this, Observer {
            if (it == AuthEmailAction.FREEZE) {
                freeze()
            }
        })


        // Init layout components
        initPhoneEditText()
        onConnectionRecovered()
    }

    private var isMaskFilled: Boolean = false

    private fun initPhoneEditText() {

        val listener = MaskedTextChangedListener("+7 ([000]) [000]-[00]-[00]", phoneEditText,
                object : MaskedTextChangedListener.ValueListener {
                    override fun onTextChanged(maskFilled: Boolean, extractedValue: String) {
                        phoneNumber = extractedValue
                        isMaskFilled = maskFilled
                    }
                }
        )

        phoneEditText.addTextChangedListener(listener)

        phoneEditText.setText(phoneNumber)


        val t = Mask
    }

    override fun onConnectionRecovered() {
        authActivity.authNavigationBar.reset()
        authActivity.authNavigationBar.nextButtonIsVisible = true
        authActivity.authNavigationBar.sudoxTagIsVisible = false
        authActivity.authNavigationBar.navigationActionCallback = {
            if (it == NavigationAction.NEXT) {
                phoneEditTextContainer.error = null

                Log.d("phoneLog", phoneNumber)

                // Запросим отправку кода у сервера (ошибки прилетят в LiveData)
                authPhoneViewModel.requestCode("7$phoneNumber")
            }
        }

        authActivity.authNavigationBar.configureComponents()
    }

    override fun freeze() {
        authActivity.authNavigationBar.freeze()
        phoneEditText.isEnabled = false
    }

    override fun unfreeze() {
        authActivity.authNavigationBar.unfreeze()
        phoneEditText.isEnabled = true
    }
}