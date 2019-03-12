package com.sudox.android.ui.main.contacts.add

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.sudox.android.common.helpers.formatPhoneByMask
import com.sudox.android.common.helpers.sendMessageViaSms
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.models.common.Errors
import com.sudox.android.data.repositories.users.CONTACTS_NAME_REGEX_ERROR
import com.sudox.android.data.repositories.users.CONTACTS_PHONE_REGEX_ERROR
import com.sudox.android.ui.main.MainActivity
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_contact.*
import javax.inject.Inject

class ContactAddFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val contactAddViewModel by lazy { getViewModel<ContactAddViewModel>(viewModelFactory) }

    // Data
    private val mainActivity by lazy { activity as MainActivity }
    private var phoneNumber: String = ""
    private var isMaskFilled: Boolean = false
    var inEditMode: Boolean = false
    var editableUser: User? = null
        set(value) {
            field = value!!.copy() // Избегаем изменения на основном обьекте
            initialEditableUser = value // Основной объект. Нужен для обнаружения изменений
        }

    var initialEditableUser: User? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (inEditMode) initAvatar()
        initToolbar()
        initNameEditText()
        initPhoneEditText()
        initDataListeners()

        // Remove hint if its fragment in edit mode
        if (inEditMode) {
            contactAddHint.visibility = View.GONE
        }
    }

    private fun initAvatar() {
        if (inEditMode) contactAvatar.bindUser(editableUser!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_contact, container, false)
    }

    private fun initDataListeners() {
        contactAddViewModel.contactAddActionsLiveData.observe(this, Observer {
            contactNameEditTextContainer.error = null
            contactPhoneEditTextContainer.error = null

            if (it == ContactAddAction.POP_BACKSTACK) {
                activity!!.onBackPressed()
            } else if (it == ContactAddAction.SHOW_USER_NOT_FOUND_ERROR) {
                contactPhoneEditTextContainer.error = getString(R.string.contact_has_not_find)

                // Show invite alert
                AlertDialog.Builder(context!!)
                        .setTitle(R.string.title_oops)
                        .setMessage(R.string.hint_no_contact_in_sudox)
                        .setPositiveButton(R.string.invite) { _, _ -> context!!.sendMessageViaSms("+7$phoneNumber", getString(R.string.invite_friend_sms)) }
                        .setNegativeButton(R.string.no_confirmation) { _, _ -> }
                        .show()
            } else if (it == ContactAddAction.SHOW_ATTEMPT_TO_ADDING_MYSELF_ERROR) {
                contactPhoneEditTextContainer.error = getString(R.string.contact_add_yourself)
            } else if (it == ContactAddAction.SHOW_USER_ALREADY_ADDED_ERROR) {
                contactPhoneEditTextContainer.error = getString(R.string.contact_has_already_added)
            }

            freezeUI(false)
        })

        contactAddViewModel.contactAddErrorsLiveData.observe(this, Observer {
            if (it == Errors.INVALID_PARAMETERS) {
                contactNameEditTextContainer.error = getString(R.string.wrong_name_format)
                if (!inEditMode) contactPhoneEditTextContainer.error = getString(R.string.wrong_phone_format)
            } else {
                contactNameEditTextContainer.error = getString(R.string.unknown_error)
                if (!inEditMode) contactPhoneEditTextContainer.error = getString(R.string.unknown_error)
            }

            freezeUI(false)
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

            freezeUI(false)
        })
    }

    @SuppressLint("SetTextI18n")
    private fun initPhoneEditText() {
        if (!inEditMode) {
            contactPhoneEditText.addTextChangedListener(MaskedTextChangedListener("+7 ([000]) [000]-[00]-[00]", contactPhoneEditText,
                    object : MaskedTextChangedListener.ValueListener {
                        override fun onTextChanged(maskFilled: Boolean, extractedValue: String) {
                            phoneNumber = extractedValue
                            isMaskFilled = maskFilled
                        }
                    }
            ))
        } else {
            contactPhoneEditText.isEnabled = false
            contactPhoneEditText.setText(formatPhoneByMask(editableUser!!.phone!!))
        }
    }

    private fun initNameEditText() {
        if (inEditMode) contactNameEditText.setText(editableUser!!.name)

        contactNameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                contactAvatar.bindLetters(contactNameEditText.text.toString())
            }
        })
    }

    private fun initToolbar() {
        mainActivity.mainToolbar.reset()
        mainActivity.mainToolbar.setNavigationOnClickListener { activity!!.onBackPressed() }

        // Texts
        if (inEditMode) {
            mainActivity.mainToolbar.setTitle(R.string.contact_edit)
            mainActivity.mainToolbar.setFeatureText(R.string.edit_save)
        } else {
            mainActivity.mainToolbar.setTitle(R.string.new_contact)
            mainActivity.mainToolbar.setFeatureText(R.string.add)
        }

        mainActivity.mainToolbar.setFeatureButtonOnClickListener(View.OnClickListener {
            if (!inEditMode) {
                contactAddViewModel.addContact(contactNameEditText.text.toString(), "7$phoneNumber")
                freezeUI(true)
            } else {
                editableUser!!.name = contactNameEditText.text.toString()

                // Запрос на редактирование ...
                contactAddViewModel.editContact(initialEditableUser!!, editableUser!!)
            }
        })
    }

    private fun freezeUI(freezeState: Boolean) {
        contactNameEditText.isEnabled = !freezeState
        if (!inEditMode) contactPhoneEditText.isEnabled = !freezeState
        mainActivity.mainToolbar.setFeatureEnabled(!freezeState)
    }
}