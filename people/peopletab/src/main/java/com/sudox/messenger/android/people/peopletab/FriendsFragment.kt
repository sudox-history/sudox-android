package com.sudox.messenger.android.people.peopletab

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
import com.sudox.messenger.android.people.peopletab.adapters.FriendsAdapter
import com.sudox.messenger.android.people.peopletab.vos.FriendVO
import com.sudox.messenger.android.people.peopletab.vos.MaybeYouKnowVO
import com.sudox.messenger.android.people.peopletab.vos.SEEN_TIME_ONLINE
import kotlinx.android.synthetic.main.fragment_friends.friendContentList
import java.util.concurrent.Semaphore

class FriendsFragment : CoreFragment(), ViewPagerFragment, ApplicationBarListener {

    private var counter = 1L
    private var counterMaybeYouKnow = 1
    private var namesCount = 0
    private var semaphore = Semaphore(1)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        friendContentList.apply {
            layoutManager = LinearLayoutManager(context!!)
            adapter = FriendsAdapter(this).apply {
                maybeYouKnowAdapter.maybeYouKnowVOs.apply {
                    add(MaybeYouKnowVO(1, "Yaroslav", true, counterMaybeYouKnow++, context.getDrawable(R.drawable.drawable_photo_2)!!))
                    add(MaybeYouKnowVO(1, "Yaroslav", true, counterMaybeYouKnow++, context.getDrawable(R.drawable.drawable_photo_2)!!))
                    add(MaybeYouKnowVO(1, "Yaroslav", true, counterMaybeYouKnow++, context.getDrawable(R.drawable.drawable_photo_2)!!))
                    add(MaybeYouKnowVO(1, "Yaroslav", true, counterMaybeYouKnow++, context.getDrawable(R.drawable.drawable_photo_2)!!))
                    add(MaybeYouKnowVO(1, "Yaroslav", true, counterMaybeYouKnow++, context.getDrawable(R.drawable.drawable_photo_2)!!))
                    add(MaybeYouKnowVO(1, "Yaroslav", true, counterMaybeYouKnow++, context.getDrawable(R.drawable.drawable_photo_2)!!))
                    add(MaybeYouKnowVO(1, "Yaroslav", true, counterMaybeYouKnow++, context.getDrawable(R.drawable.drawable_photo_2)!!))
                    add(MaybeYouKnowVO(1, "Yaroslav", true, counterMaybeYouKnow++, context.getDrawable(R.drawable.drawable_photo_2)!!))
                    add(MaybeYouKnowVO(1, "Yaroslav", true, counterMaybeYouKnow++, context.getDrawable(R.drawable.drawable_photo_2)!!))
                    add(MaybeYouKnowVO(1, "Yaroslav", true, counterMaybeYouKnow++, context.getDrawable(R.drawable.drawable_photo_2)!!))
                    add(MaybeYouKnowVO(1, "Yaroslav", true, counterMaybeYouKnow++, context.getDrawable(R.drawable.drawable_photo_2)!!))
                    add(MaybeYouKnowVO(1, "Yaroslav", true, counterMaybeYouKnow++, context.getDrawable(R.drawable.drawable_photo_2)!!))
                    add(MaybeYouKnowVO(1, "Yaroslav", true, counterMaybeYouKnow++, context.getDrawable(R.drawable.drawable_photo_2)!!))
                }

                friendClickCallback = {}
                maybeYouKnowAdapter.userClickCallback = {
                    maybeYouKnowAdapter.maybeYouKnowVOs.add(MaybeYouKnowVO(
                            1, "Gandon", true, counterMaybeYouKnow++, context.getDrawable(R.drawable.drawable_photo_1)!!
                    ))
                }


                maybeYouKnowAdapter.removeUserCallback = {
                    maybeYouKnowAdapter.maybeYouKnowVOs.removeItemAt(0)
                }

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
                requestsVOs.add(FriendVO(4, "Pozdnyakov", SEEN_TIME_ONLINE, counter++, context.getDrawable(R.drawable.drawable_photo_3)!!))
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