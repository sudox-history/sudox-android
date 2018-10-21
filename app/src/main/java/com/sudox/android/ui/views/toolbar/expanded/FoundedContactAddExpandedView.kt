package com.sudox.android.ui.views.toolbar.expanded

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import com.sudox.android.R
import com.sudox.android.common.helpers.drawAvatar
import com.sudox.android.common.helpers.drawCircleBitmap
import com.sudox.android.data.database.model.Contact
import com.sudox.android.data.models.avatar.AvatarInfo
import com.sudox.android.data.models.avatar.impl.ColorAvatarInfo
import kotlinx.android.synthetic.main.expanded_contact_add_view.view.*
import kotlinx.android.synthetic.main.founded_contact_add_layout.view.*

class FoundedContactAddExpandedView : ExpandedView {

    var contactAddButtonClickCallback: (() -> (Unit))? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        turnBlackOverlay = false

        // Inflate view
        inflate(context, R.layout.founded_contact_add_layout, this)

        // Bind button
        foundedContactAddButton.setOnClickListener { contactAddButtonClickCallback?.invoke() }
    }

    fun bindData(contact: Contact) {
        foundedContactName.text = contact.name
        foundedContactNickname.text = contact.nickname

        // Find avatar info
        val avatarInfo = AvatarInfo.parse(contact.photo)

        // Show avatar
        if (avatarInfo is ColorAvatarInfo) {
            drawCircleBitmap(context, drawAvatar(
                    text = contact.buildShortName(),
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