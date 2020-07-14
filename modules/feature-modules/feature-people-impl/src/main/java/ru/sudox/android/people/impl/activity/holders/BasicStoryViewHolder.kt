package ru.sudox.android.people.impl.activity.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.sudox.android.core.ui.planet.PlanetView
import ru.sudox.android.people.impl.R
import ru.sudox.simplelists.BasicListHolder
import ru.sudox.simplelists.model.BasicListViewObject

/**
 * Базовый ViewHolder для историй.
 *
 * @param fragment Связанный фрагмент
 */
open class BasicStoryViewHolder<T : BasicListViewObject<*, *>>(
    view: View,
    private val fragment: Fragment
) : BasicListHolder<T>(view) {

    val avatar: ImageView = view.findViewById(R.id.storyAvatar)
    val planetSatellite: ImageView = view.findViewById(R.id.storyPlanetSatellite)
    val planet: PlanetView = view.findViewById(R.id.storyPlanetView)
    val publisher: TextView = view.findViewById(R.id.storyPublisher)

    override fun recycleView() {
        Glide.with(fragment).clear(avatar)
    }
}