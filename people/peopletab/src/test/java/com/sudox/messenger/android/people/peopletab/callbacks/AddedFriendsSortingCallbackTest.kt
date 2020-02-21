package com.sudox.messenger.android.people.peopletab.callbacks

import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.messenger.android.people.common.vos.SEEN_TIME_ONLINE
import com.sudox.messenger.android.people.peopletab.vos.AddedFriendVO
import com.sudox.messenger.android.people.peopletab.vos.headers.IMPORTANCE_OPTION_TAG
import com.sudox.messenger.android.people.peopletab.vos.headers.NAME_OPTION_TAG
import com.sudox.messenger.android.people.peopletab.vos.headers.ONLINE_OPTION_TAG
import edu.emory.mathcs.backport.java.util.Collections
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito

class AddedFriendsSortingCallbackTest : Assert() {

    private var viewListAdapter: ViewListAdapter<*>? = null
    private var sortingCallback: AddedFriendsSortingCallback? = null

    @Before
    fun setUp() {
        viewListAdapter = Mockito.mock(ViewListAdapter::class.java)
        sortingCallback = AddedFriendsSortingCallback(viewListAdapter!!)
    }

    @Test
    fun testComparingByImportance() {
        Mockito.`when`(viewListAdapter!!.getSortingTypeByHeader(anyInt(), anyInt())).thenReturn(IMPORTANCE_OPTION_TAG)

        val list = arrayListOf(
                AddedFriendVO(1, "Test 1", 1L, 1L, 1),
                AddedFriendVO(2, "Test 2", 1L, 1L, 2),
                AddedFriendVO(3, "Test 3", 1L, 1L, 3),
                AddedFriendVO(4, "Test 4", 1L, 1L, 4),
                AddedFriendVO(5, "Test 5", 1L, 1L, 5)
        )

        Collections.sort(list) { first, second ->
            sortingCallback!!.compare(first as AddedFriendVO, second as AddedFriendVO)
        }

        assertEquals(5, list[0].userId)
        assertEquals(4, list[1].userId)
        assertEquals(3, list[2].userId)
        assertEquals(2, list[3].userId)
        assertEquals(1, list[4].userId)
    }

    @Test
    fun testComparingByOnline() {
        Mockito.`when`(viewListAdapter!!.getSortingTypeByHeader(anyInt(), anyInt())).thenReturn(ONLINE_OPTION_TAG)

        val list = arrayListOf(
                AddedFriendVO(1, "A", 1L, 1L, 1),
                AddedFriendVO(2, "B", 2L, 1L, 1),
                AddedFriendVO(3, "C", 3L, 1L, 1),
                AddedFriendVO(4, "D", SEEN_TIME_ONLINE, 1L, 1),
                AddedFriendVO(5, "E", SEEN_TIME_ONLINE, 1L, 1)
        )

        Collections.sort(list) { first, second ->
            sortingCallback!!.compare(first as AddedFriendVO, second as AddedFriendVO)
        }

        assertEquals(4, list[0].userId)
        assertEquals(5, list[1].userId)
        assertEquals(3, list[2].userId)
        assertEquals(2, list[3].userId)
        assertEquals(1, list[4].userId)
    }

    @Test
    fun testComparingByName() {
        Mockito.`when`(viewListAdapter!!.getSortingTypeByHeader(anyInt(), anyInt())).thenReturn(NAME_OPTION_TAG)

        val list = arrayListOf(
                AddedFriendVO(1, "E", 1L, 1L, 1),
                AddedFriendVO(2, "D", 1L, 1L, 2),
                AddedFriendVO(4, "C", 1L, 1L, 4),
                AddedFriendVO(3, "C", 1L, 1L, 3),
                AddedFriendVO(5, "B", 1L, 1L, 5)
        )

        Collections.sort(list) { first, second ->
            sortingCallback!!.compare(first as AddedFriendVO, second as AddedFriendVO)
        }

        assertEquals(5, list[0].userId)
        assertEquals(4, list[1].userId)
        assertEquals(3, list[2].userId)
        assertEquals(2, list[3].userId)
        assertEquals(1, list[4].userId)
    }
}