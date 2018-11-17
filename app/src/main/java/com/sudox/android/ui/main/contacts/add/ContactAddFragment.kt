package com.sudox.android.ui.main.contacts.add

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.common.helpers.WHITESPACES_REMOVE_REGEX
import com.sudox.android.data.models.Errors
import com.sudox.android.data.repositories.main.CONTACTS_NAME_REGEX_ERROR
import com.sudox.android.data.repositories.main.CONTACTS_PHONE_REGEX_ERROR
import com.sudox.android.ui.main.common.BaseReconnectFragment
import com.sudox.design.helpers.drawAvatar
import kotlinx.android.synthetic.main.fragment_add_contact.*
import javax.inject.Inject

class ContactAddFragment @Inject constructor() : BaseReconnectFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var inviteFriendDialogFragment: InviteFriendDialogFragment

    private var firstCharsOfName = CharArray(2)
    private lateinit var contactAddViewModel: ContactAddViewModel
    var phoneNumber: String = ""
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
                inviteFriendDialogFragment.show(childFragmentManager,"inviteFriend5")
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
        setupDefaultAvatar()

        // Listen connection status
        listenForConnection()
    }

    private fun setupDefaultAvatar() {
        Glide.with(this)
                .load(R.drawable.rectangle_white)
                .apply(RequestOptions.circleCropTransform())
                .into(contactAddAvatar)
    }

    override fun showConnectionStatus(isConnect: Boolean) {
        if (isConnect) {
            contactAddToolbar.title = getString(R.string.new_contact)
        } else {
            contactAddToolbar.title = getString(R.string.wait_for_connect)
        }
    }

    private fun initEditTexts() {
        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                if (text.isNotEmpty()) {
                    val firstChars = text
                            .trim()
                            .replace(WHITESPACES_REMOVE_REGEX, " ")
                            .split(" ")

                    if (firstChars.size == 1 && firstChars[0].isNotEmpty()) {
                        if (firstCharsOfName[0] != firstChars[0][0] || firstCharsOfName[0] != 0.toChar()) {
                            firstCharsOfName[0] = firstChars[0][0]
                            firstCharsOfName[1] = 0.toChar()

                            // Update avatar
                            drawAvatar("${firstCharsOfName[0]}")
                        }
                    } else if (firstChars.size >= 2 && firstChars[0].isNotEmpty() && firstChars[1].isNotEmpty()) {
                        if (firstCharsOfName[0] != firstChars[0][0] || firstCharsOfName[1] != firstChars[1][0]) {
                            firstCharsOfName[0] = firstChars[0][0]
                            firstCharsOfName[1] = firstChars[1][0]

                            // Update avatar
                            drawAvatar("${firstCharsOfName[0]}${firstCharsOfName[1]}")
                        }
                    }
                } else {
                    setupDefaultAvatar()
                }
            }
        })

        phoneEditText.addTextChangedListener(MaskedTextChangedListener("+7 ([000]) [000]-[00]-[00]", phoneEditText,
                object : MaskedTextChangedListener.ValueListener {
                    override fun onTextChanged(maskFilled: Boolean, extractedValue: String) {
                        phoneNumber = extractedValue
                        isMaskFilled = maskFilled
                    }
                }
        ))
    }

    private fun drawAvatar(text: String) {
        Glide.with(context!!)
                .load(drawAvatar(text, "#FFFFFF", "#FFFFFF", Color.BLACK))
                .apply(RequestOptions.circleCropTransform())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        contactAddAvatar.setImageDrawable(resource)

                        // Ignore
                        return true
                    }
                }).preload().onLoadStarted(ContextCompat.getDrawable(context!!, R.drawable.rectangle_white))
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