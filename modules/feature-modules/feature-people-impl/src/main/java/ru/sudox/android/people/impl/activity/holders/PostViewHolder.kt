package ru.sudox.android.people.impl.activity.holders

import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ru.sudox.android.core.ui.avatar.loadAvatar
import ru.sudox.android.core.ui.mityushkinlayout.MityushkinLayout
import ru.sudox.android.people.impl.R
import ru.sudox.android.people.impl.activity.viewobject.PostViewObject
import ru.sudox.android.people.impl.people.holders.BasicPeopleViewHolder
import ru.sudox.android.time.formatters.FullTimeFormatter
import ru.sudox.android.time.timestampToString
import ru.sudox.simplelists.model.BasicListItem

/**
 * ViewHolder для поста.
 *
 * @param fragment Связанный фрагмент.
 * @param mediaViewPool ViewPool для компонентов медиа вложений.
 */
class PostViewHolder(
    view: View,
    private val fragment: Fragment,
    private val mediaViewPool: RecyclerView.RecycledViewPool
) : BasicPeopleViewHolder<PostViewObject>(
    view = view,
    fragment = fragment
) {

    private val text = view.findViewById<TextView>(R.id.postText)
    private val mediaList = view.findViewById<MityushkinLayout>(R.id.postMediaList)

    init {
        onlineBadge!!.visibility = View.GONE
        mediaList.setRecycledViewPool(mediaViewPool)
    }

    override fun bind(item: BasicListItem<PostViewObject>, changePayload: List<Any>?) {
        super.bind(item, changePayload)

        avatar.loadAvatar(fragment, vo!!.publisherId, vo!!.publisherName, vo!!.publisherAvatarId)
        description.text = timestampToString(itemView.context, formatter = FullTimeFormatter, timestamp = vo!!.publishTime)
        name.text = vo!!.publisherName
        text.text = vo!!.postText
    }
}