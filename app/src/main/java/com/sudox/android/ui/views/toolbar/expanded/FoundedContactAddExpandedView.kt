package com.sudox.android.ui.views.toolbar.expanded

import android.content.Context
import android.util.AttributeSet
import com.sudox.android.ApplicationLoader
import com.sudox.android.R
import com.sudox.android.common.helpers.drawAvatar
import com.sudox.android.common.helpers.drawCircleBitmap
import com.sudox.android.common.helpers.getTwoFirstLetters
import com.sudox.android.data.database.model.User
import com.sudox.android.data.models.Errors
import com.sudox.android.data.models.avatar.AvatarInfo
import com.sudox.android.data.models.avatar.impl.ColorAvatarInfo
import com.sudox.android.data.repositories.main.ContactsRepository
import kotlinx.android.synthetic.main.view_contact_add_founded_expanded.view.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class FoundedContactAddExpandedView : ExpandedView {

    @Inject
    lateinit var contactsRepository: ContactsRepository
    lateinit var user: User

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        turnBlackOverlay = false

        // Inflate view
        inflate(context, R.layout.view_contact_add_founded_expanded, this)

        // Заинжектим здесь все к хуям
        ApplicationLoader.component.inject(this)

        // Bind button
        foundedContactAddButton.setOnClickListener { addContact() }
    }

    private fun addContact() {
        contactsRepository.addContact(user.uid, {
            GlobalScope.launch(Dispatchers.Main) { hide() }
        }) {
            if (it == Errors.INVALID_USER) {
                contactAddFoundedStatusExpandedView.showMessage(context.getString(R.string.contact_has_already_added))
            } else {
                contactAddFoundedStatusExpandedView.showMessage(context.getString(R.string.unknown_error))
            }
        }
    }

    fun bindData(user: User) = GlobalScope.launch(Dispatchers.Main) {
        this@FoundedContactAddExpandedView.user = user

        // Bind data
        foundedContactName.text = user.name
        foundedContactNickname.text = user.nickname

        // Find avatar info
        val avatarInfo = AvatarInfo.parse(user.avatar)

        // Show avatar
        if (avatarInfo is ColorAvatarInfo) {
            drawCircleBitmap(context, drawAvatar(
                    text = user.name.getTwoFirstLetters(),
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