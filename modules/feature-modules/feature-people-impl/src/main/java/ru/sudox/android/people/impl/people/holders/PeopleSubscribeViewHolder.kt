package ru.sudox.android.people.impl.people.holders

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.sudox.android.core.ui.avatar.loadAvatar
import ru.sudox.android.core.ui.people.BasicPeopleViewHolder
import ru.sudox.android.people.impl.R
import ru.sudox.android.people.impl.people.viewobjects.PeopleSubscribeViewObject
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
    fragment = fragment,
    canShowOnlineBadge = false
) {

    private val message = view.findViewById<TextView>(R.id.peopleMessage)
    private val moreButton = view.findViewById<Button>(R.id.peopleItemFirstButton)

    init {
        moreButton.background = null
    }

    override fun bind(item: BasicListItem<PeopleSubscribeViewObject>, changePayload: List<Any>?) {
        super.bind(item, changePayload)

        name.text = vo!!.name
        avatar.loadAvatar(fragment, vo!!.userId, vo!!.name, vo!!.avatarId)

        if (vo!!.status == null || !vo!!.isOnline) {
            setSeenTimeDescription(vo!!.isOnline, vo!!.onlineTimestamp)
        } else {
            message.text = vo!!.status
        }
    }
}