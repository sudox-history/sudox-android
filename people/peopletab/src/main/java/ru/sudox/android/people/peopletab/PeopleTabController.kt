package ru.sudox.android.people.peopletab

import android.view.View
import ru.sudox.android.core.controllers.ViewListController
import ru.sudox.android.people.common.vos.SEEN_TIME_ONLINE
import ru.sudox.android.people.peopletab.adapters.PeopleTabAdapter
import ru.sudox.android.people.peopletab.vos.AddedFriendVO
import ru.sudox.android.people.peopletab.vos.FriendRequestVO
import ru.sudox.android.people.peopletab.vos.MaybeYouKnowVO
import ru.sudox.design.viewlist.ViewList

class PeopleTabController : ViewListController<PeopleTabAdapter>() {

    override fun bindView(view: View) {
        adapter!!.apply {
            friendsRequestsVOs.apply {
                add(FriendRequestVO(2, "undefined.7887", SEEN_TIME_ONLINE, 2L, "Привет, как дела?", 1L))
            }

            maybeYouKnowAdapter.maybeYouKnowVOs.apply {
                add(MaybeYouKnowVO(5, "andy", SEEN_TIME_ONLINE, 5, 21))
                add(MaybeYouKnowVO(1, "kerjen", 1L, 1, 18))
            }

            addedFriendsVOs.apply {
                add(AddedFriendVO(4, "Максим Митюшкин", SEEN_TIME_ONLINE, 4, "Я строю новый ЦОД каждый день", 1))
                add(AddedFriendVO(6, "Jeremy Clarkson", 1583520378000L, 6, "I am a still small voice of calm and reason.", 1))
            }

            subscriptionsVOs.apply {}
        }
    }

    override fun getAdapter(viewList: ViewList): PeopleTabAdapter {
        return PeopleTabAdapter()
    }

    override fun isChild(): Boolean {
        return true
    }
}