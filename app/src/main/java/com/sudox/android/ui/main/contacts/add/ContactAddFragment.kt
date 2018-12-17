package com.sudox.android.ui.main.contacts.add

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.data.models.common.Errors
import com.sudox.android.data.repositories.main.CONTACTS_NAME_REGEX_ERROR
import com.sudox.android.data.repositories.main.CONTACTS_PHONE_REGEX_ERROR
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_add_contact.*
import kotlinx.android.synthetic.main.fragment_auth_phone.*
import javax.inject.Inject

class ContactAddFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var contactAddViewModel: ContactAddViewModel

    // Data
    private var phoneNumber: String = ""
    private var isMaskFilled: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contactAddViewModel = getViewModel(viewModelFactory)

        return inflater.inflate(R.layout.fragment_add_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initToolbar()
        initAvatar()
        initPhoneEditText()
        initDataListeners()

        // Super!
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initDataListeners() {
        contactAddViewModel.contactAddActionsLiveData.observe(this, Observer {
            contactNameEditTextContainer.error = null
            contactPhoneEditTextContainer.error = null

            if (it == ContactAddAction.POP_BACKSTACK) {
                activity!!.onBackPressed()
            } else if (it == ContactAddAction.SHOW_USER_NOT_FOUND_ERROR) {
                contactPhoneEditTextContainer.error = getString(R.string.contact_has_not_find)
            } else if (it == ContactAddAction.SHOW_ATTEMPT_TO_ADDING_MYSELF_ERROR) {
                contactPhoneEditTextContainer.error = getString(R.string.contact_add_yourself)
            } else if (it == ContactAddAction.SHOW_USER_ALREADY_ADDED_ERROR) {
                contactPhoneEditTextContainer.error = getString(R.string.contact_has_already_added)
            }
        })

        contactAddViewModel.contactAddErrorsLiveData.observe(this, Observer {
            if (it == Errors.INVALID_PARAMETERS) {
                contactNameEditTextContainer.error = getString(R.string.wrong_name_format)
                contactPhoneEditTextContainer.error = getString(R.string.wrong_phone_format)
            } else {
                contactNameEditTextContainer.error = getString(R.string.unknown_error)
                contactPhoneEditTextContainer.error = getString(R.string.unknown_error)
            }
        })

        contactAddViewModel.contactAddRegexErrorsLiveData.observe(this, Observer {
            contactNameEditTextContainer.error = null
            contactPhoneEditTextContainer.error = null

            it!!.forEach {
                if (it == CONTACTS_NAME_REGEX_ERROR) {
                    contactNameEditTextContainer.error = getString(R.string.wrong_name_format)
                } else if (it == CONTACTS_PHONE_REGEX_ERROR) {
                    contactPhoneEditTextContainer.error = getString(R.string.wrong_phone_format)
                }
            }
        })
    }

    private fun initPhoneEditText() {
        contactPhoneEditText.addTextChangedListener(MaskedTextChangedListener("+7 ([000]) [000]-[00]-[00]", contactPhoneEditText,
                object : MaskedTextChangedListener.ValueListener {
                    override fun onTextChanged(maskFilled: Boolean, extractedValue: String) {
                        phoneNumber = extractedValue
                        isMaskFilled = maskFilled
                    }
                }
        ))
    }

    private fun initAvatar() {
        contactNameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                contactAvatar.bindLetters(contactNameEditText.text.toString())
            }
        })
    }

    private fun initToolbar() {
        contactAddToolbar.setFeatureButtonOnClickListener(View.OnClickListener {
            contactAddViewModel.addContact(contactNameEditText.text.toString(), "7$phoneNumber")
        })

        // Back-pressing ...
        contactAddToolbar.setNavigationOnClickListener { activity!!.onBackPressed() }
    }
}