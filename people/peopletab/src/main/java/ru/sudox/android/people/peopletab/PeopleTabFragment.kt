package ru.sudox.android.people.peopletab

import android.content.Context
import ru.sudox.design.viewlist.ViewList
import ru.sudox.android.core.fragments.ViewListFragment
import ru.sudox.android.core.tabs.TabsChildFragment
import ru.sudox.android.people.common.vos.SEEN_TIME_ONLINE
import ru.sudox.android.people.peopletab.adapters.PeopleTabAdapter
import ru.sudox.android.people.peopletab.vos.AddedFriendVO
import ru.sudox.android.people.peopletab.vos.FriendRequestVO
import ru.sudox.android.people.peopletab.vos.MaybeYouKnowVO
import ru.sudox.android.people.peopletab.vos.appbar.PeopleTabAppBarVO

class PeopleTabFragment : ViewListFragment<PeopleTabAdapter>(), TabsChildFragment {

    init {
        appBarVO = PeopleTabAppBarVO()
    }

    override fun getAdapter(viewList: ViewList): PeopleTabAdapter {
        return PeopleTabAdapter().apply {
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

    override fun getTitle(context: Context): String {
        return context.getString(R.string.people)
    }
}