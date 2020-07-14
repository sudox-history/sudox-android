package ru.sudox.android.people.impl.people.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.sudox.android.core.ui.lists.SectionedScreenListAdapter
import ru.sudox.android.people.impl.R
import ru.sudox.android.people.impl.people.MAYBE_YOU_KNOW_SECTION_ORDER
import ru.sudox.android.people.impl.people.REQUESTS_SECTION_ORDER
import ru.sudox.android.people.impl.people.SUBSCRIBE_VIEW_TYPE
import ru.sudox.android.people.impl.people.holders.PeopleMaybeYouKnowListHolder
import ru.sudox.android.people.impl.people.holders.PeopleRequestViewHolder
import ru.sudox.android.people.impl.people.holders.PeopleSubscribeViewHolder
import ru.sudox.android.people.impl.people.holders.PeopleSubscriptionViewHolder
import ru.sudox.android.people.impl.people.viewobjects.PeopleRequestViewObject
import ru.sudox.simplelists.BasicListHolder

/**
 * Адаптер экрана People
 *
 * @param fragment Связанный фрагмент
 * @param maybeYouKnowAdapter Адаптер для списка возможно знакомых людей,
 * @param onRequestAccepted Функция, вызываемая при принятии запроса на подписку
 * @param onRequestRejected Функция, вызываемая при отзыве запроса на подписку.
 */
class PeopleAdapter(
    context: Context,
    private val fragment: Fragment,
    private val maybeYouKnowAdapter: RecyclerView.Adapter<*>,
    private val onRequestAccepted: (PeopleRequestViewObject) -> (Unit),
    private val onRequestRejected: (PeopleRequestViewObject) -> (Unit)
) : SectionedScreenListAdapter(context) {

    override fun createOtherViewHolder(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BasicListHolder<*> {
        return when (viewType) {
            MAYBE_YOU_KNOW_SECTION_ORDER -> {
                val container = inflater.inflate(R.layout.item_people_maybe_you_know_list, parent, false) as ViewGroup
                val recyclerView = container.getChildAt(0) as RecyclerView

                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.adapter = maybeYouKnowAdapter

                PeopleMaybeYouKnowListHolder(container)
            }
            REQUESTS_SECTION_ORDER -> PeopleRequestViewHolder(
                inflater.inflate(R.layout.item_people_request, parent, false),
                fragment,
                onRequestAccepted,
                onRequestRejected
            )
            SUBSCRIBE_VIEW_TYPE -> PeopleSubscribeViewHolder(inflater.inflate(R.layout.item_people_subscribe, parent, false), fragment)
            else -> PeopleSubscriptionViewHolder(inflater.inflate(R.layout.item_people_subscribe, parent, false), fragment)
        }
    }
}