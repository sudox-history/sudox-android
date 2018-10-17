package com.sudox.android.ui.views.toolbar.expanded

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sudox.android.R
import com.sudox.android.common.helpers.drawAvatar
import com.sudox.android.data.database.model.Contact
import com.sudox.android.data.models.avatar.AvatarInfo
import com.sudox.android.data.models.avatar.impl.ColorAvatarInfo
import kotlinx.android.synthetic.main.expanded_contact_add_view.view.*
import kotlinx.android.synthetic.main.founded_contact_layout.view.*

class ContactAddExpandedView : ExpandedView {

    constructor(context: Context) : super(context, true)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private lateinit var contact: Contact

    init {
        inflate(context, R.layout.expanded_contact_add_view, this)
    }

    override fun clear() {
        nicknameEditText.setText("")

    }

    /**
     *  Публичная функция для отслеживания ввода почты, отправляет калбекс введенной почтой
     */
    fun listenForEmail(emailCallback: (String) -> (Unit)) {
        //TODO: Разобраться с названием этого EditText
        nicknameEditText.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                emailCallback(nicknameEditText.text.toString())
                handled = true
            }
            handled
        }
    }

    fun setUpContact(contact: Contact) {
        this.contact = contact

        foundedContactName.text = contact.name

        //TODO: Заменить название
        foundedContactStatus.text = contact.nickname

        val avatar = AvatarInfo.parse(contact.photo)

        if(avatar is ColorAvatarInfo){
            Glide.with(this)
                    .load(drawAvatar(contact.buildShortName(),
                            avatar.firstColor,
                            avatar.secondColor))
                    .apply(RequestOptions().circleCrop())
                    .into(foundedContactAvatar)
        } else {
            Glide.with(this)
                    .load(avatar.string)
                    .apply(RequestOptions().circleCrop())
                    .into(foundedContactAvatar)
        }

        contactAddHint.visibility = View.GONE
        foundedContactLayout.show()
    }

    fun listenSelectContact(contactSelectCallback: (Contact) -> (Unit)) = foundedContactLayout.setOnClickListener {
        contactSelectCallback(contact)
    }

    fun listenAddContact(contactAddCallback: (Contact) -> (Unit)) = foundedContactAdd.setOnClickListener {
        contactAddCallback(contact)
    }

}