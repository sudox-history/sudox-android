package ru.sudox.android.core.ui.people

import android.text.SpannableStringBuilder
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.text.color
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.sudox.android.core.ui.R
import ru.sudox.android.core.ui.badge.BadgeView
import ru.sudox.android.time.formatters.SeenTimeFormatter
import ru.sudox.android.time.timestampToString
import ru.sudox.simplelists.BasicListHolder
import ru.sudox.simplelists.model.BasicListItem
import ru.sudox.simplelists.model.BasicListViewObject

/**
 * Базовый ViewHolder для отображения записи пользователя
 *
 * @param view View, которая будет помещена внутрь
 * @param canShowOnlineBadge Можно ли отображать индикатор онлайна?
 * @param avatarViewId ID View аватарки
 * @param descriptionViewId ID View статуса
 * @param onlineBadgeViewId ID View значка онлайна
 * @param nameViewId ID View имени
 * @param fragment Связанный фрагмент
 */
open class BasicPeopleViewHolder<T : BasicListViewObject<*, *>>(
    view: View,
    canShowOnlineBadge: Boolean = true,
    @IdRes avatarViewId: Int = R.id.peopleAvatar,
    @IdRes descriptionViewId: Int = R.id.peopleMessage,
    @IdRes onlineBadgeViewId: Int = R.id.peopleOnlineBadge,
    @IdRes nameViewId: Int = R.id.peopleName,
    private val fragment: Fragment
) : BasicListHolder<T>(view) {

    private val onlineText = SpannableStringBuilder().color(itemView.context.getColor(R.color.colorAccent)) {
        append(itemView.context.getString(R.string.people_online))
    }

    val onlineBadge: BadgeView = view.findViewById<BadgeView>(onlineBadgeViewId).apply {
        if (!canShowOnlineBadge) {
            visibility = View.GONE
        }
    }

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

    /**
     * Выставляет статус объекта в качестве описания
     *
     * @param isOnline В сети ли объект?
     * @param seenTime Время последнего захода объекта в сеть
     */
    fun setSeenTimeDescription(isOnline: Boolean, seenTime: Long) {
        description.text = if (!isOnline) {
            timestampToString(itemView.context, formatter = SeenTimeFormatter, timestamp = seenTime)
        } else {
            onlineText
        }
    }
}