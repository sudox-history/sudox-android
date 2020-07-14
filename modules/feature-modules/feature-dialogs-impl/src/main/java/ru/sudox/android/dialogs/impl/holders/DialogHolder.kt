package ru.sudox.android.dialogs.impl.holders

import android.text.SpannableStringBuilder
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.sudox.android.core.ui.avatar.loadAvatar
import ru.sudox.android.core.ui.badge.BadgeView
import ru.sudox.android.core.ui.formatNumber
import ru.sudox.android.dialogs.impl.R
import ru.sudox.android.dialogs.impl.viewobjects.DIALOG_ONLINE_STATUS_CHANGED
import ru.sudox.android.dialogs.impl.viewobjects.DialogViewObject
import ru.sudox.android.time.formatters.ShortTimeFormatter
import ru.sudox.android.time.timestampToString
import ru.sudox.simplelists.BasicListHolder
import ru.sudox.simplelists.model.BasicListItem

/**
 * Holder для отображения элемента диалога
 *
 * @param onClicked Функция, которая будет вызвана при клике
 * @param fragment Связанный фрагмент.
 */
class DialogHolder(
    view: View,
    onClicked: (DialogViewObject) -> (Unit),
    private val fragment: Fragment
) : BasicListHolder<DialogViewObject>(view) {

    private var vo: DialogViewObject? = null
    private var nameView: TextView = view.findViewById(R.id.dialogName)
    private var timeView: TextView = view.findViewById(R.id.dialogTime)
    private var lastMessageView: TextView = view.findViewById(R.id.dialogLastMessage)
    private var muteIconView: ImageView = view.findViewById(R.id.dialogMuteIcon)
    private var unreadMessagesBadge: BadgeView = view.findViewById(R.id.dialogUnreadMessagesBadge)
    private val avatarBadge: BadgeView = view.findViewById(R.id.dialogAvatarBadge)
    private var avatarView: ImageView = view.findViewById(R.id.dialogAvatar)
    private var hintColor = ContextCompat.getColor(view.context, R.color.colorTextHint)
    private var controlNormalColor = ContextCompat.getColor(view.context, R.color.colorControlNormal)
    private var accentColor = ContextCompat.getColor(view.context, R.color.colorAccent)
    private var youPrefix = view.resources.getString(R.string.you_prefix)

    init {
        view.setOnClickListener { onClicked(vo!!) }
    }

    override fun bind(item: BasicListItem<DialogViewObject>, changePayload: List<Any>?) {
        vo = item.viewObject

        if (vo!!.dialogLastMessage != null) {
            val builder = SpannableStringBuilder()

            if (vo!!.isSentByMe) {
                builder.color(hintColor) { append(youPrefix).append(": ") }
            } else if (vo!!.lastMessageSenderName != null) {
                builder.color(hintColor) { append(vo!!.lastMessageSenderName).append(": ") }
            }

            if (vo!!.isDialogMuted) {
                builder.color(controlNormalColor) { append(vo!!.dialogLastMessage) }
            } else {
                builder.append(vo!!.dialogLastMessage)
            }

            lastMessageView.text = builder
        }

        if (vo!!.isDialogMuted) {
            unreadMessagesBadge.color = hintColor
            muteIconView.visibility = View.VISIBLE
            lastMessageView.maxLines = 1
        } else {
            unreadMessagesBadge.color = accentColor
            muteIconView.visibility = View.GONE
            lastMessageView.maxLines = 2
        }

        if (vo!!.dialogUnreadMessages > 0) {
            unreadMessagesBadge.visibility = View.VISIBLE
            unreadMessagesBadge.text = formatNumber(vo!!.dialogUnreadMessages)
        } else {
            unreadMessagesBadge.visibility = View.GONE
            unreadMessagesBadge.text = null
        }

        timeView.text = timestampToString(itemView.context, formatter = ShortTimeFormatter, timestamp = vo!!.dialogTime)
        avatarBadge.toggle(vo!!.isUserOnline, DIALOG_ONLINE_STATUS_CHANGED, changePayload)
        avatarView.loadAvatar(fragment, vo!!.dialogId, vo!!.dialogName, vo!!.dialogAvatarId)
        nameView.text = vo!!.dialogName
    }

    override fun recycleView() {
        Glide.with(fragment).clear(avatarView)
        vo = null
    }
}