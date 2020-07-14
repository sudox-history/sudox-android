package ru.sudox.android.people.impl.activity.holders

import android.view.View
import androidx.fragment.app.Fragment
import ru.sudox.android.core.ui.avatar.loadAvatar
import ru.sudox.android.people.impl.R
import ru.sudox.android.people.impl.activity.viewobject.MyStoryViewObject
import ru.sudox.simplelists.model.BasicListItem

/**
 * ViewHolder для истории, созданной пользователем
 *
 * @param fragment Связанный фрагмент.
 */
class MyStoryViewHolder(
    view: View,
    private val fragment: Fragment
) : BasicStoryViewHolder<MyStoryViewObject>(view, fragment) {

    private val yourStoryText = view.context.getString(R.string.your_story)

    override fun bind(item: BasicListItem<MyStoryViewObject>, changePayload: List<Any>?) {
        val vo = item.viewObject!!

        if (vo.isPublished) {
            planetSatellite.setImageResource(0)
            planet.setActive(true)
        } else {
            planetSatellite.setImageResource(R.drawable.drawable_add_story)
            planet.setActive(false)
        }

        avatar.loadAvatar(fragment, vo.publisherId, vo.publisherName, vo.publisherAvatarId)
        publisher.text = yourStoryText
    }
}