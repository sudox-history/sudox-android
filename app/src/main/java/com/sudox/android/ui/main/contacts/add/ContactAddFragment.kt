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
import com.sudox.android.ui.main.common.BaseReconnectFragment
import kotlinx.android.synthetic.main.fragment_add_contact.*
import javax.inject.Inject

class ContactAddFragment @Inject constructor() : BaseReconnectFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var contactAddViewModel: ContactAddViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contactAddViewModel = getViewModel(viewModelFactory)

        listenForConnection()

        return inflater.inflate(R.layout.fragment_add_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbarListeners()
        initMainScreen()
    }

    override fun showConnectionStatus(isConnect: Boolean) {
        if (isConnect) {
            contactAddToolbar.title = getString(R.string.new_contact)
        } else {
            contactAddToolbar.title = getString(R.string.wait_for_connect)
        }
    }

    var phoneNumber: String = ""
    var isMaskFilled: Boolean = false

    private fun initMainScreen() {

        val listener = MaskedTextChangedListener("+7 ([000]) [000]-[00]-[00]", phoneEditText,
                object : MaskedTextChangedListener.ValueListener {
                    override fun onTextChanged(maskFilled: Boolean, extractedValue: String) {
                        phoneNumber = extractedValue
                        isMaskFilled = maskFilled
                    }
                }
        )

        phoneEditText.addTextChangedListener(listener)

        Glide.with(this)
                .load(R.drawable.rectangle_white)
                .apply(RequestOptions.circleCropTransform())
                .into(avatar)
    }

    private fun initToolbarListeners() {
        contactAddToolbar.setFeatureButtonOnClickListener(View.OnClickListener {
            contactAddViewModel
                    .contactsRepository
                    .addContact(nameEditText.text.toString(), "7$phoneNumber").observe(this, Observer { response ->
                        if (response == 0) {
                            fragmentManager!!.popBackStack()
                        } else {
                            phoneEditTextContainer.error = when (response) {
                                Errors.INVALID_PARAMETERS -> getString(R.string.wrong_phone_format)
                                else -> getString(R.string.unknown_error)
                            }
                        }
                    })
        })

        contactAddToolbar.setNavigationOnClickListener {
            fragmentManager!!.popBackStack()
        }
    }


}