package com.sudox.android.ui.auth.phone

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.data.models.common.Errors
import com.sudox.android.ui.auth.AuthActivity
import com.sudox.android.ui.auth.common.BaseAuthFragment
import com.sudox.android.ui.auth.phone.enums.AuthEmailAction
import com.sudox.design.navigation.toolbar.enums.NavigationAction
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.fragment_auth_phone.*
import javax.inject.Inject
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.slots.Slot
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.FormattedTextChangeListener
import ru.tinkoff.decoro.slots.PredefinedSlots


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

        requestSmsPermission()

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

    private fun initPhoneEditText() {
        val formatWatcher = MaskFormatWatcher(
                MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        )

        formatWatcher.installOn(phoneEditText)

        formatWatcher.setCallback(object: FormattedTextChangeListener{
            override fun beforeFormatting(oldValue: String?, newValue: String?): Boolean { return false }
            override fun onTextFormatted(formatter: FormatWatcher?, newFormattedText: String?) {
                phoneNumber = newFormattedText
            }

        })

        phoneEditText.setText(phoneNumber)
    }

    private fun requestSmsPermission() {
        val permission = Manifest.permission.RECEIVE_SMS
        val grant = ContextCompat.checkSelfPermission(activity!!, permission)
        if (grant != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOfNulls<String>(1)
            permissions[0] = permission
            ActivityCompat.requestPermissions(activity!!, permissions, 1)
        }
    }

    override fun onConnectionRecovered() {
        authActivity.authNavigationBar.reset()
        authActivity.authNavigationBar.nextButtonIsVisible = true
        authActivity.authNavigationBar.sudoxTagIsVisible = false
        authActivity.authNavigationBar.navigationActionCallback = {
            if (it == NavigationAction.NEXT) {
                phoneEditTextContainer.error = null
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