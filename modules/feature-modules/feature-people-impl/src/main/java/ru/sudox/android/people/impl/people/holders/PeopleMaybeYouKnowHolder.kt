package ru.sudox.android.people.impl.people.holders

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.sudox.android.core.ui.avatar.loadAvatar
import ru.sudox.android.people.impl.R
import ru.sudox.android.people.impl.people.viewobjects.MAYBE_YOU_KNOW_ONLINE_STATUS_CHANGED
import ru.sudox.android.people.impl.people.viewobjects.PeopleMaybeYouKnowViewObject
import ru.sudox.simplelists.model.BasicListItem

/**
 * ViewHolder для записи возможно знакомого человека
 *
 * @param fragment Связанный фрагмент
 * @param onRemoved Функция, вызываемая при удалении записи пользователем.
 */
class PeopleMaybeYouKnowHolder(
    view: View,
    private val fragment: Fragment,
    onRemoved: (PeopleMaybeYouKnowViewObject) -> (Unit)
) : BasicPeopleViewHolder<PeopleMaybeYouKnowViewObject>(
    view = view,
    fragment = fragment,
    avatarViewId = R.id.peopleMaybeYouKnowAvatar,
    descriptionViewId = R.id.peopleMaybeYouKnowDescription,
    onlineBadgeViewId = R.id.peopleMaybeYouKnowOnlineBadge,
    nameViewId = R.id.peopleMaybeYouKnowName
) {

    private val adIcon = view.findViewById<TextView>(R.id.peopleMaybeYouKnowAdIcon)
    private val removeButton = view.findViewById<Button>(R.id.peopleMaybeYouKnowRemoveButton)

    init {
        removeButton.setOnClickListener { onRemoved(vo!!) }
        removeButton.background = null
    }

    override fun bind(item: BasicListItem<PeopleMaybeYouKnowViewObject>, changePayload: List<Any>?) {
        super.bind(item, changePayload)

        name.text = vo!!.name
        avatar.loadAvatar(fragment, vo!!.userId, vo!!.name, vo!!.avatarId)
        description.text = vo!!.description ?: itemView.context.resources.getQuantityString(
            R.plurals.mutual_friends,
            vo!!.mutualFriends,
            vo!!.mutualFriends
        )

        adIcon.visibility = if (vo!!.isAd) {
            View.VISIBLE
        } else {
            View.GONE
        }

        onlineBadge!!.toggle(vo!!.isOnline, MAYBE_YOU_KNOW_ONLINE_STATUS_CHANGED, changePayload)
    }
}