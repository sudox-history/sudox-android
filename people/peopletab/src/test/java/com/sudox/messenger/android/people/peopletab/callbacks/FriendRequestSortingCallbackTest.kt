package com.sudox.messenger.android.people.peopletab.callbacks

import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.messenger.android.people.peopletab.vos.AddedFriendVO
import com.sudox.messenger.android.people.peopletab.vos.FriendRequestVO
import com.sudox.messenger.android.people.peopletab.vos.headers.IMPORTANCE_OPTION_TAG
import edu.emory.mathcs.backport.java.util.Collections
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

class FriendRequestSortingCallbackTest : Assert() {

    private var viewListAdapter: ViewListAdapter<*>? = null
    private var sortingCallback: FriendRequestSortingCallback? = null

    @Before
    fun setUp() {
        viewListAdapter = Mockito.mock(ViewListAdapter::class.java)
        sortingCallback = FriendRequestSortingCallback(viewListAdapter!!)
    }

    @Test
    fun testComparing() {
        val list = arrayListOf(
                FriendRequestVO(1, "Test 1", 1L, 1L, "", 1L),
                FriendRequestVO(2, "Test 2", 1L, 1L, "", 2L),
                FriendRequestVO(3, "Test 3", 1L, 1L, "", 3L),
                FriendRequestVO(4, "Test 4", 1L, 1L, "", 4L),
                FriendRequestVO(5, "Test 5", 1L, 1L, "", 5L)
        )

        Collections.sort(list) { first, second ->
            sortingCallback!!.compare(first as FriendRequestVO, second as FriendRequestVO)
        }

        assertEquals(5, list[0].userId)
        assertEquals(4, list[1].userId)
        assertEquals(3, list[2].userId)
        assertEquals(2, list[3].userId)
        assertEquals(1, list[4].userId)
    }
}