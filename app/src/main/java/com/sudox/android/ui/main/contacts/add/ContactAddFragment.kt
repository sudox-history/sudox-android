package com.sudox.android.ui.main.contacts.add

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.data.models.Errors
import com.sudox.android.data.repositories.main.CONTACTS_NAME_REGEX_ERROR
import com.sudox.android.data.repositories.main.CONTACTS_PHONE_REGEX_ERROR
import com.sudox.android.ui.main.common.BaseReconnectFragment
import kotlinx.android.synthetic.main.fragment_add_contact.*
import javax.inject.Inject

class ContactAddFragment @Inject constructor() : BaseReconnectFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var contactAddViewModel: ContactAddViewModel
    private var phoneNumber: String = ""
    private var isMaskFilled: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contactAddViewModel = getViewModel(viewModelFactory)

        return inflater.inflate(R.layout.fragment_add_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactAddViewModel.contactAddActionLiveData.observe(this, Observer {
            nameEditTextContainer.error = null
            phoneEditTextContainer.error = null

            if (it == ContactAddAction.POP_BACKSTACK) {
                fragmentManager!!.popBackStack()
            } else if (it == ContactAddAction.SHOW_USER_NOT_FOUND_ERROR) {
                // TODO: Show notify
            }
        })

        // Слушаем заказанные ViewModel действия ...
        contactAddViewModel.contactAddRegexErrorsCallback = {
            nameEditTextContainer.error = null
            phoneEditTextContainer.error = null

            it.forEach {
                if (it == CONTACTS_NAME_REGEX_ERROR) {
                    nameEditTextContainer.error = getString(R.string.wrong_name_format)
                } else if (it == CONTACTS_PHONE_REGEX_ERROR) {
                    phoneEditTextContainer.error = getString(R.string.wrong_phone_format)
                }
            }
        }

        contactAddViewModel.contactAddErrorsLiveData.observe(this, Observer {
            if (it == Errors.INVALID_PARAMETERS) {
                nameEditTextContainer.error = getString(R.string.wrong_name_format)
                phoneEditTextContainer.error = getString(R.string.wrong_phone_format)
            } else {
                nameEditTextContainer.error = getString(R.string.unknown_error)
                phoneEditTextContainer.error = getString(R.string.unknown_error)
            }
        })

        initToolbarListeners()
        initEditTexts()
        initMainScreen()

        // Listen connection status
        listenForConnection()
    }

    override fun showConnectionStatus(isConnect: Boolean) {
        if (isConnect) {
            contactAddToolbar.title = getString(R.string.new_contact)
        } else {
            contactAddToolbar.title = getString(R.string.wait_for_connect)
        }
    }

    private fun initMainScreen() {
        Glide.with(this)
                .load(R.drawable.rectangle_white)
                .apply(RequestOptions.circleCropTransform())
                .into(avatar)
    }

    private fun initEditTexts() {
        phoneEditText.addTextChangedListener(MaskedTextChangedListener("+7 ([000]) [000]-[00]-[00]", phoneEditText,
                object : MaskedTextChangedListener.ValueListener {
                    override fun onTextChanged(maskFilled: Boolean, extractedValue: String) {
                        phoneNumber = extractedValue
                        isMaskFilled = maskFilled
                    }
                }
        ))
    }

    private fun initToolbarListeners() {
        contactAddToolbar.setNavigationOnClickListener { fragmentManager!!.popBackStack() }
        contactAddToolbar.setFeatureButtonOnClickListener(View.OnClickListener {
            val name = nameEditText.text.toString()
            val phone = "7$phoneNumber"

            contactAddViewModel.addContact(name, phone)
        })
    }
}