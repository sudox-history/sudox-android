package com.sudox.android.ui.main.messages.dialogs

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.data.database.model.User
import com.sudox.android.data.models.avatar.AvatarInfo
import com.sudox.android.data.models.avatar.impl.ColorAvatarInfo
import com.sudox.design.helpers.drawAvatar
import com.sudox.design.helpers.drawCircleBitmap
import com.sudox.design.helpers.getTwoFirstLetters
import kotlinx.android.synthetic.main.item_dialog.view.*


class DialogsAdapter(val context: Context): RecyclerView.Adapter<DialogsAdapter.Holder>(){

    var items: List<User> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): Holder {
        return Holder(LayoutInflater
                .from(context)
                .inflate(R.layout.item_dialog, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(p0: Holder, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val avatar = view.avatar!!
        val name = view.name!!
        val nickname = view.lastMessage!!

        fun bindData(user: User) {
            bindAvatar(user)

            // Bind others data ...
            name.text = user.name
            nickname.text = user.nickname
        }

        private fun bindAvatar(user: User) {
            val avatarInfo = AvatarInfo.parse(user.avatar)

            // aka GradientAvatar
            if (avatarInfo is ColorAvatarInfo) {
                drawCircleBitmap(view.context, drawAvatar(
                        text = user.name.getTwoFirstLetters(),
                        firstColor = avatarInfo.firstColor,
                        secondColor = avatarInfo.secondColor), avatar)
            }
        }
    }
}