package ru.sudox.android.people.impl.people

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.sudox.android.core.ui.lists.model.SectionVO
import ru.sudox.android.people.impl.R
import ru.sudox.android.people.impl.people.viewobjects.PeopleMaybeYouKnowViewObject
import ru.sudox.android.people.impl.people.viewobjects.PeopleRequestViewObject
import ru.sudox.android.people.impl.people.viewobjects.PeopleSubscribeViewObject
import ru.sudox.android.people.impl.people.viewobjects.PeopleSubscriptionViewObject
import ru.sudox.simplelists.model.BasicListItem

const val REQUESTS_SECTION_ORDER = 0
const val MAYBE_YOU_KNOW_SECTION_ORDER = 1
const val SUBSCRIPTIONS_AND_SUBSCRIBES_SECTION_ORDER = 2
const val SUBSCRIBE_VIEW_TYPE = 2
const val SUBSCRIPTION_VIEW_TYPE = 3

/**
 * ViewModel для вкладки People.
 */
class PeopleViewModel @ViewModelInject constructor() : ViewModel() {

    val requestsLiveData = MutableLiveData<List<BasicListItem<PeopleRequestViewObject>>?>()
    val maybeYouKnowLiveData = MutableLiveData<ArrayList<BasicListItem<PeopleMaybeYouKnowViewObject>>?>()
    val subscribesLiveData = MutableLiveData<List<BasicListItem<PeopleSubscribeViewObject>>>()
    val subscriptionsLiveData = MutableLiveData<List<BasicListItem<PeopleSubscriptionViewObject>>>()

    val requestsSectionVO = SectionVO(titleRes = R.string.requests_section_title, countValue = 35)
    val maybeYouKnowSectionVO = SectionVO(titleRes = R.string.maybe_you_know_section_title, canCollapse = true)
    val subscribesAndSubscriptionsSectionVO = SectionVO(
        typesMenuRes = R.menu.menu_subscriptions_and_subscribes,
        defaultTypeId = R.id.subscribesItem,
        sortsMenuRes = R.menu.menu_subscriptions_and_subscribes_sort,
        defaultSortId = R.id.subscribesItem
    )

    init {
        requestsLiveData.postValue(
            listOf(
                BasicListItem(
                    REQUESTS_SECTION_ORDER,
                    PeopleRequestViewObject("1", "Антон Янкин", "Привет, я по поводу домашнего задания", "1", true)
                )
            )
        )

        maybeYouKnowLiveData.postValue(
            arrayListOf(
                BasicListItem(
                    MAYBE_YOU_KNOW_SECTION_ORDER,
                    PeopleMaybeYouKnowViewObject("1", "undefined.7887", 21, null, "2", true, false)
                ),
                BasicListItem(
                    MAYBE_YOU_KNOW_SECTION_ORDER,
                    PeopleMaybeYouKnowViewObject("1", "kotlinovsky", 0, "Co-owner of Sudox", "4", false, true)
                ),
                BasicListItem(
                    MAYBE_YOU_KNOW_SECTION_ORDER,
                    PeopleMaybeYouKnowViewObject("1", "isp", 2, null, "3", false, false)
                ),
                BasicListItem(
                    MAYBE_YOU_KNOW_SECTION_ORDER,
                    PeopleMaybeYouKnowViewObject("1", "andy", 5, null, "5", false, false)
                ),
                BasicListItem(
                    MAYBE_YOU_KNOW_SECTION_ORDER,
                    PeopleMaybeYouKnowViewObject("1", "JemeryClarkson", 5, "Blogger", "6", false, true)
                )
            )
        )
    }

    fun loadSubscribes() {
        subscribesLiveData.postValue(
            listOf(
                BasicListItem(
                    SUBSCRIBE_VIEW_TYPE,
                    PeopleSubscribeViewObject("1", "Максим Митюшкин", "Co-owner of Sudox", "4", 1593611760000L, false)
                ),
                BasicListItem(
                    SUBSCRIBE_VIEW_TYPE,
                    PeopleSubscribeViewObject("1", "Никита Казанцев", "Just chilling", "3", 0, true)
                ),
                BasicListItem(
                    SUBSCRIBE_VIEW_TYPE,
                    PeopleSubscribeViewObject("1", "BMW M760Li", null, "8", 0, true)
                )
            )
        )
    }

    fun loadSubscriptions() {
        subscriptionsLiveData.postValue(
            listOf(
                BasicListItem(
                    SUBSCRIPTION_VIEW_TYPE,
                    PeopleSubscriptionViewObject("1", "Дмитрий Парамонов", "11", 1593611760000L, false)
                )
            )
        )
    }
}