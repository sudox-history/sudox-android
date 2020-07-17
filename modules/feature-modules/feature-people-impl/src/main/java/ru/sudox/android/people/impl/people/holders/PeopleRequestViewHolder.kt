package ru.sudox.android.people.impl.people.holders

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.sudox.android.core.ui.avatar.loadAvatar
import ru.sudox.android.core.ui.people.BasicPeopleViewHolder
import ru.sudox.android.people.impl.R
import ru.sudox.android.people.impl.people.viewobjects.PeopleRequestViewObject
import ru.sudox.android.people.impl.people.viewobjects.REQUEST_ONLINE_STATUS_CHANGED
import ru.sudox.simplelists.model.BasicListItem

/**
 * ViewHolder для запроса подписки
 *
 * @param fragment Привязанный фрагмент
 * @param onAccepted Функция, вызываемая при принятии запроса
 * @param onRejected Функция, вызываемая при отзыве запроса.
 */
class PeopleRequestViewHolder(
    view: View,
    private val fragment: Fragment,
    private val onAccepted: (PeopleRequestViewObject) -> (Unit),
    private val onRejected: (PeopleRequestViewObject) -> (Unit)
) : BasicPeopleViewHolder<PeopleRequestViewObject>(
    view = view,
    fragment = fragment
) {

    private val message = view.findViewById<TextView>(R.id.peopleMessage)
    private val acceptButton = view.findViewById<Button>(R.id.peopleItemFirstButton)
    private val rejectButton = view.findViewById<Button>(R.id.peopleItemSecondButton)
    private val newFriendRequestText = itemView.context.getString(R.string.new_friend_request)

    init {
        acceptButton.setOnClickListener { onAccepted(vo!!) }
        rejectButton.setOnClickListener { onRejected(vo!!) }
    }

    override fun bind(item: BasicListItem<PeopleRequestViewObject>, changePayload: List<Any>?) {
        super.bind(item, changePayload)

        onlineBadge.toggle(vo!!.isOnline, REQUEST_ONLINE_STATUS_CHANGED, changePayload)
        avatar.loadAvatar(fragment, vo!!.userId, vo!!.name, vo!!.avatarId)
        message.text = vo!!.message ?: newFriendRequestText
        name.text = vo!!.name
    }
}