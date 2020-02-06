package com.sudox.messenger.android.friends

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.viewPager.ViewPagerFragment
import com.sudox.messenger.android.friends.adapters.FriendsAdapter
import com.sudox.messenger.android.friends.vos.FriendVO
import kotlinx.android.synthetic.main.fragment_friends.friendContentList
import java.util.concurrent.Semaphore

class FriendsFragment : CoreFragment(), ViewPagerFragment, ApplicationBarListener {

    private var counter = 1L
    private var namesCount = 0
    private var semaphore = Semaphore(1)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        friendContentList.apply {
            layoutManager = LinearLayoutManager(context!!)
            adapter = FriendsAdapter(this).apply {
                acceptRequestCallback = {
                    semaphore.acquire()

                    if (onlineVOs.size() > 0) {
                        onlineVOs.removeItemAt(0)
                    } else if (offlineVOs.size() > 0) {
                        offlineVOs.removeItemAt(0)
                    }

                    semaphore.release()
                }

                rejectRequestCallback = {
                    semaphore.acquire()

                    if (onlineVOs.size() < 3) {
                        onlineVOs.add(FriendVO(3, ('Z' - namesCount++).toString(), 3L, counter++, context.getDrawable(R.drawable.drawable_photo_3)!!))
                    } else if (offlineVOs.size() < 3) {
                        offlineVOs.add(FriendVO(3, ('Z' - namesCount++).toString(), 3L, counter++, context.getDrawable(R.drawable.drawable_photo_3)!!))
                    }

                    semaphore.release()
                }

//                onlineVO.add(FriendVO(3, "A", 3L, counter++, context.getDrawable(R.drawable.drawable_photo_3)!!))
//                onlineVO.add(FriendVO(3, "Z", 3L, counter++, context.getDrawable(R.drawable.drawable_photo_3)!!))

                requestsVOs.add(FriendVO(1, "Yaroslav", 1L, counter++, context.getDrawable(R.drawable.drawable_photo_2)!!))
                requestsVOs.add(FriendVO(2, "Anton", 2L, counter++, context.getDrawable(R.drawable.drawable_photo_1)!!))
                requestsVOs.add(FriendVO(3, "Mr. Pozdnyakov", 3L, counter++, context.getDrawable(R.drawable.drawable_photo_3)!!))
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