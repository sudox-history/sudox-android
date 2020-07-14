package ru.sudox.android.people.impl.people.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.sudox.android.core.ui.badge.BadgeView
import ru.sudox.android.people.impl.R
import ru.sudox.simplelists.BasicListHolder
import ru.sudox.simplelists.model.BasicListItem
import ru.sudox.simplelists.model.BasicListViewObject

/**
 * Базовый ViewHolder для отображения записи на экране People
 *
 * @param view View, которая будет помещена внутрь
 * @param avatarViewId ID View аватарки
 * @param descriptionViewId ID View статуса
 * @param onlineBadgeViewId ID View значка онлайна
 * @param nameViewId ID View имени
 * @param fragment Связанный фрагмент
 */
open class BasicPeopleViewHolder<T : BasicListViewObject<*, *>>(
    view: View,
    @IdRes avatarViewId: Int = R.id.peopleAvatar,
    @IdRes descriptionViewId: Int = R.id.peopleMessage,
    @IdRes onlineBadgeViewId: Int = R.id.peopleOnlineBadge,
    @IdRes nameViewId: Int = R.id.peopleName,
    private val fragment: Fragment
) : BasicListHolder<T>(view) {

    val onlineBadge: BadgeView? = view.findViewById(onlineBadgeViewId)
    val avatar: ImageView = view.findViewById(avatarViewId)
    val description: TextView = view.findViewById(descriptionViewId)
    val name: TextView = view.findViewById(nameViewId)
    var vo: T? = null
        private set

    override fun bind(item: BasicListItem<T>, changePayload: List<Any>?) {
        vo = item.viewObject!!
    }

    override fun recycleView() {
        Glide.with(fragment).clear(avatar)
        vo = null
    }
}