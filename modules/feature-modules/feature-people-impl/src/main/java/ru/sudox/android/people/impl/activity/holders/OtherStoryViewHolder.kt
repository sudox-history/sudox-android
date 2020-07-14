package ru.sudox.android.people.impl.activity.holders

import android.content.res.ColorStateList
import android.view.View
import androidx.fragment.app.Fragment
import ru.sudox.android.core.ui.avatar.loadAvatar
import ru.sudox.android.people.impl.R
import ru.sudox.android.people.impl.activity.viewobject.OtherStoryViewObject
import ru.sudox.simplelists.model.BasicListItem

/**
 * ViewHolder, для историй, опубликованный другими пользователями
 *
 * @param fragment Связанный фрагмент
 */
class OtherStoryViewHolder(
    view: View,
    private val fragment: Fragment
) : BasicStoryViewHolder<OtherStoryViewObject>(view, fragment) {

    private val eventColor = itemView.context.getColor(R.color.storyEventColor)

    override fun bind(item: BasicListItem<OtherStoryViewObject>, changePayload: List<Any>?) {
        val vo = item.viewObject!!

        avatar.loadAvatar(fragment, vo.publisherId, vo.publisherName, vo.publisherAvatarId)
        publisher.text = vo.publisherName

        when {
            vo.isStoryViewed -> {
                planetSatellite.imageTintList = null
                planetSatellite.setImageResource(0)
                planet.setActive(false)
            }
            vo.isStoryEvent -> {
                planetSatellite.setImageResource(R.drawable.ic_round_star)
                planetSatellite.imageTintList = ColorStateList.valueOf(eventColor)
                planet.setActive(true, eventColor)
            }
            else -> {
                planetSatellite.imageTintList = null
                planetSatellite.setImageResource(0)
                planet.setActive(true)
            }
        }
    }
}