package com.sudox.messenger.android.friends

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.viewPager.ViewPagerFragment
import com.sudox.messenger.android.friends.vos.FriendVO
import kotlinx.android.synthetic.main.fragment_friends.friendContentList

class FriendsFragment : CoreFragment(), ViewPagerFragment, ApplicationBarListener {

    private var counter = 1L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        friendContentList.apply {
            layoutManager = LinearLayoutManager(context!!)
            adapter = FriendsAdapter(context!!).apply {
                registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        if (positionStart == 0 && !friendContentList.canScrollVertically(-1)) {
                            friendContentList.scrollToPosition(0)
                        }

                        super.onItemRangeInserted(positionStart, itemCount)
                    }
                })

                acceptRequestCallback = {
                    requests.add(FriendVO(1, "Yaroslav", 1L, counter++, context.getDrawable(R.drawable.drawable_photo_2)!!))
                }

                rejectRequestCallback = {
                    requests.add(FriendVO(3, "Mr. Pozdnyakov", 3L, counter++, context.getDrawable(R.drawable.drawable_photo_3)!!))
                }

                requests.add(FriendVO(1, "Yaroslav", 1L, counter++, context.getDrawable(R.drawable.drawable_photo_2)!!))
                requests.add(FriendVO(2, "Anton", 2L, counter++, context.getDrawable(R.drawable.drawable_photo_1)!!))
                requests.add(FriendVO(3, "Mr. Pozdnyakov", 3L, counter++, context.getDrawable(R.drawable.drawable_photo_3)!!))
            }
        }
    }

    override fun onButtonClicked(tag: Int) {
    }

    override fun onPageSelected(activity: CoreActivity) {
        activity.getApplicationBarManager().let {
            it.reset(false)
            it.setListener(this)
            it.toggleIconButtonAtEnd(R.drawable.ic_search)
        }
    }

    override fun getPageTitle(context: Context): CharSequence? {
        return context.getString(R.string.friends)
    }
}