package ru.sudox.android.dialogs.impl.search.holders

import android.view.View
import androidx.fragment.app.Fragment
import ru.sudox.android.core.ui.avatar.loadAvatar
import ru.sudox.android.core.ui.people.BasicPeopleViewHolder
import ru.sudox.android.dialogs.impl.search.viewobjects.FoundChatViewObject
import ru.sudox.simplelists.model.BasicListItem

/**
 * ViewHolder для найденного чата.
 *
 * @param view View, которая будет содержаться в Holder'е
 * @param fragment Связанный фрагмент.
 */
class FoundChatViewHolder(
    view: View,
    private val fragment: Fragment
) : BasicPeopleViewHolder<FoundChatViewObject>(
    view = view,
    fragment = fragment,
    canShowOnlineBadge = false
) {

    override fun bind(item: BasicListItem<FoundChatViewObject>, changePayload: List<Any>?) {
        super.bind(item, changePayload)

        setSeenTimeDescription(vo!!.isUserOnline, vo!!.userSeenTime)
        avatar.loadAvatar(fragment, vo!!.userId, vo!!.userName, vo!!.userAvatarId)
        name.text = vo!!.userName
    }
}