package com.sudox.android.ui.main.contacts.view

import android.content.Context
import android.util.AttributeSet
import com.sudox.android.data.models.avatar.AvatarInfo
import com.sudox.android.data.models.avatar.impl.ColorAvatarInfo
import com.sudox.android.data.models.users.dto.SearchUserDTO
import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.android.data.repositories.main.ContactsRepository
import com.sudox.design.helpers.drawAvatar
import com.sudox.design.helpers.drawCircleBitmap
import com.sudox.design.helpers.getTwoFirstLetters
import com.sudox.design.navigation.toolbar.expanded.ExpandedView
import kotlinx.android.synthetic.main.view_contact_add_founded_expanded.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.android.Main
import kotlinx.coroutines.launch
import javax.inject.Inject

class FoundedContactAddExpandedView : ExpandedView {

    @Inject
    lateinit var contactsRepository: ContactsRepository

    @Inject
    lateinit var accountRepository: AccountRepository

    private lateinit var searchUserDTO: SearchUserDTO

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

//    init {
//        turnBlackOverlay = false
//
//        // Inflate view
//        inflate(context, R.layout.view_contact_add_founded_expanded, this)
//
//        // Заинжектим здесь все к хуям
//        ApplicationLoader.component.inject(this)
//
//        // Bind button
//        foundedContactAddButton.setOnClickListener { addContact() }
//    }

//    private fun addContact() {
//        contactsRepository.addContact(searchUserDTO.id, {
//            GlobalScope.launch(Dispatchers.Main) { hide() }
//        }) {
//            runBlocking {
//                if (it == Errors.INVALID_USER) {
//                    val account = accountRepository.getAccount().await() ?: return@runBlocking
//
//                    if (account.id != searchUserDTO.id) {
//                        contactAddFoundedStatusExpandedView.showMessage(context.getString(R.string.contact_has_already_added))
//                    } else {
//                        contactAddFoundedStatusExpandedView.showMessage(context.getString(R.string.contact_add_yourself))
//                    }
//                } else {
//                    contactAddFoundedStatusExpandedView.showMessage(context.getString(R.string.unknown_error))
//                }
//            }
//        }
//    }

    fun bindData(userGetByEmail: SearchUserDTO) = GlobalScope.launch(Dispatchers.Main) {
        this@FoundedContactAddExpandedView.searchUserDTO = userGetByEmail

        // Bind data
        foundedContactName.text = userGetByEmail.name
        foundedContactNickname.text = userGetByEmail.nickname

        // Find avatar info
        val avatarInfo = AvatarInfo.parse(userGetByEmail.photo)

        // Show avatar
        if (avatarInfo is ColorAvatarInfo) {
            drawCircleBitmap(context, drawAvatar(
                    text = userGetByEmail.name.getTwoFirstLetters(),
                    firstColor = avatarInfo.firstColor,
                    secondColor = avatarInfo.secondColor
            ), foundedContactAvatar)
        } else {
            // TODO: Others ...
        }
    }

    override fun clear() {
        foundedContactName.text = ""
        foundedContactNickname.text = ""
    }
}