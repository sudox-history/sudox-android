package ru.sudox.android.people.impl.people.holders

import android.text.SpannableStringBuilder
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.text.color
import androidx.fragment.app.Fragment
import ru.sudox.android.core.ui.avatar.loadAvatar
import ru.sudox.android.people.impl.R
import ru.sudox.android.people.impl.people.viewobjects.PeopleSubscriptionViewObject
import ru.sudox.android.people.impl.people.viewobjects.SUBSCRIPTION_ONLINE_STATUS_CHANGED
import ru.sudox.android.time.formatters.SeenTimeFormatter
import ru.sudox.android.time.timestampToString
import ru.sudox.simplelists.model.BasicListItem

/**
 * ViewHolder для подписки
 *
 * @param fragment Связанный фрагмент.
 */
class PeopleSubscriptionViewHolder(
    view: View,
    private val fragment: Fragment
) : BasicPeopleViewHolder<PeopleSubscriptionViewObject>(
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

    override fun bind(item: BasicListItem<PeopleSubscriptionViewObject>, changePayload: List<Any>?) {
        super.bind(item, changePayload)

        message.text = if (!vo!!.isOnline) {
            timestampToString(itemView.context, formatter = SeenTimeFormatter, timestamp = vo!!.onlineTimestamp)
        } else {
            onlineText
        }

        onlineBadge!!.toggle(vo!!.isOnline, SUBSCRIPTION_ONLINE_STATUS_CHANGED, changePayload)
        avatar.loadAvatar(fragment, vo!!.userId, vo!!.name, vo!!.avatarId)
        name.text = vo!!.name
    }
}