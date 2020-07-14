package ru.sudox.android.people.impl.people.holders

import android.text.SpannableStringBuilder
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.text.color
import androidx.fragment.app.Fragment
import ru.sudox.android.core.ui.avatar.loadAvatar
import ru.sudox.android.people.impl.R
import ru.sudox.android.people.impl.people.viewobjects.PeopleSubscribeViewObject
import ru.sudox.android.people.impl.people.viewobjects.SUBSCRIBE_ONLINE_STATUS_CHANGED
import ru.sudox.android.time.formatters.SeenTimeFormatter
import ru.sudox.android.time.timestampToString
import ru.sudox.simplelists.model.BasicListItem

/**
 * ViewHolder для подписки
 *
 * @param fragment Связанный фрагмент.
 */
class PeopleSubscribeViewHolder(
    view: View,
    private val fragment: Fragment
) : BasicPeopleViewHolder<PeopleSubscribeViewObject>(
    view = view,
    fragment = fragment
) {

    private val message = view.findViewById<TextView>(R.id.peopleMessage)
    private val moreButton = view.findViewById<Button>(R.id.peopleItemFirstButton)
    private val onlineText = SpannableStringBuilder().color(itemView.context.getColor(R.color.colorAccent)) {
        append(itemView.context.getString(R.string.people_online))
    }

    init {
        moreButton.background = null
    }

    override fun bind(item: BasicListItem<PeopleSubscribeViewObject>, changePayload: List<Any>?) {
        super.bind(item, changePayload)

        name.text = vo!!.name
        message.text = if (!vo!!.isOnline) {
            timestampToString(itemView.context, formatter = SeenTimeFormatter, timestamp = vo!!.onlineTimestamp)
        } else if (vo!!.status != null) {
            vo!!.status
        } else {
            onlineText
        }

        avatar.loadAvatar(fragment, vo!!.userId, vo!!.name, vo!!.avatarId)
        onlineBadge!!.toggle(vo!!.isOnline, SUBSCRIBE_ONLINE_STATUS_CHANGED, changePayload)
    }
}