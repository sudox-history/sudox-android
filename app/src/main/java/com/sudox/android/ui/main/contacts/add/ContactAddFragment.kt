package com.sudox.android.ui.main.contacts.add

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
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_add_contact.*
import javax.inject.Inject

class ContactAddFragment @Inject constructor() : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var contactAddViewModel: ContactAddViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contactAddViewModel = getViewModel(viewModelFactory)

        return inflater.inflate(R.layout.fragment_add_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbarListeners()
        initMainListeners()

        Glide.with(this)
                .load(R.drawable.rectangle_white)
                .apply(RequestOptions.circleCropTransform())
                .into(avatar)
    }

    var phoneNumber: String = ""
    var isMaskFilled: Boolean = false

    private fun initMainListeners() {
        val listener = MaskedTextChangedListener("+7 ([000]) [000]-[00]-[00]", phoneEditText,
                object : MaskedTextChangedListener.ValueListener {
                    override fun onTextChanged(maskFilled: Boolean, extractedValue: String) {
                        phoneNumber = extractedValue
                        isMaskFilled = maskFilled
                    }
                }
        )

        phoneEditText.addTextChangedListener(listener)
    }

    private fun initToolbarListeners() {
        contactAddToolbar.setFeatureButtonOnClickListener(View.OnClickListener {


            contactAddViewModel
                    .contactsRepository
                    .addContact(nameEditText.text.toString(), phoneEditText.text.toString(), {
                        fragmentManager!!.popBackStack()
                    }, {
                        phoneEditTextContainer.error = it.toString()
                    })

        })

        contactAddToolbar.setNavigationOnClickListener {
            fragmentManager!!.popBackStack()
        }
    }


}