package ru.sudox.android.people.impl.people.holders

import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import ru.sudox.android.core.ui.avatar.loadAvatar
import ru.sudox.android.core.ui.people.BasicPeopleViewHolder
import ru.sudox.android.people.impl.R
import ru.sudox.android.people.impl.people.viewobjects.PeopleSubscriptionViewObject
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
    fragment = fragment,
    canShowOnlineBadge = false
) {

    private val moreButton = view.findViewById<Button>(R.id.peopleItemFirstButton)

    init {
        moreButton.background = null
    }

    override fun bind(item: BasicListItem<PeopleSubscriptionViewObject>, changePayload: List<Any>?) {
        super.bind(item, changePayload)

        setSeenTimeDescription(vo!!.isOnline, vo!!.onlineTimestamp)
        avatar.loadAvatar(fragment, vo!!.userId, vo!!.name, vo!!.avatarId)
        name.text = vo!!.name
    }
}