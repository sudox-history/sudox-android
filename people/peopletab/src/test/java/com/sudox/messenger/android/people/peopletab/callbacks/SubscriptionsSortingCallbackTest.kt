package com.sudox.messenger.android.people.peopletab.callbacks

import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.messenger.android.people.peopletab.vos.SubscriptionVO
import com.sudox.messenger.android.people.peopletab.vos.headers.FAVORITE_OPTION_TAG
import com.sudox.messenger.android.people.peopletab.vos.headers.NAME_OPTION_TAG
import com.sudox.messenger.android.people.peopletab.vos.headers.POPULAR_OPTION_TAG
import edu.emory.mathcs.backport.java.util.Collections
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito

class SubscriptionsSortingCallbackTest : Assert() {

    private var viewListAdapter: ViewListAdapter<*>? = null
    private var sortingCallback: SubscriptionsSortingCallback? = null

    @Before
    fun setUp() {
        viewListAdapter = Mockito.mock(ViewListAdapter::class.java)
        sortingCallback = SubscriptionsSortingCallback(viewListAdapter!!)
    }

    @Test
    fun testComparingByFavorite() {
        Mockito.`when`(viewListAdapter!!.getSortingTypeByHeader(anyInt(), anyInt())).thenReturn(FAVORITE_OPTION_TAG)

        val list = arrayListOf(
                SubscriptionVO(1, "Test 1", 1L, 1L, null, 1, 1),
                SubscriptionVO(2, "Test 2", 1L, 1L, null, 2, 1),
                SubscriptionVO(3, "Test 3", 1L, 1L, null, 3, 1),
                SubscriptionVO(4, "Test 4", 1L, 1L, null, 4, 1),
                SubscriptionVO(5, "Test 5", 1L, 1L, null, 5, 1)
        )

        Collections.sort(list) { first, second ->
            sortingCallback!!.compare(first as SubscriptionVO, second as SubscriptionVO)
        }

        assertEquals(5, list[0].userId)
        assertEquals(4, list[1].userId)
        assertEquals(3, list[2].userId)
        assertEquals(2, list[3].userId)
        assertEquals(1, list[4].userId)
    }

    @Test
    fun testComparingByPopular() {
        Mockito.`when`(viewListAdapter!!.getSortingTypeByHeader(anyInt(), anyInt())).thenReturn(POPULAR_OPTION_TAG)

        val list = arrayListOf(
                SubscriptionVO(1, "Test 1", 1L, 1L, null, 1, 1),
                SubscriptionVO(2, "Test 2", 1L, 1L, null, 1, 2),
                SubscriptionVO(3, "Test 3", 1L, 1L, null, 1, 3),
                SubscriptionVO(4, "Test 4", 1L, 1L, null, 1, 4),
                SubscriptionVO(5, "Test 5", 1L, 1L, null, 1, 5)
        )

        Collections.sort(list) { first, second ->
            sortingCallback!!.compare(first as SubscriptionVO, second as SubscriptionVO)
        }

        assertEquals(5, list[0].userId)
        assertEquals(4, list[1].userId)
        assertEquals(3, list[2].userId)
        assertEquals(2, list[3].userId)
        assertEquals(1, list[4].userId)
    }

    @Test
    fun testComparingByName() {
        Mockito.`when`(viewListAdapter!!.getSortingTypeByHeader(anyInt(), anyInt())).thenReturn(NAME_OPTION_TAG)

        val list = arrayListOf(
                SubscriptionVO(1, "E", 1L, 1L, null, 1, 1),
                SubscriptionVO(2, "D", 1L, 1L, null, 1, 2),
                SubscriptionVO(4, "C", 1L, 1L, null, 1, 3),
                SubscriptionVO(3, "C", 1L, 1L, null, 1, 4),
                SubscriptionVO(5, "B", 1L, 1L, null, 1, 5)
        )

        Collections.sort(list) { first, second ->
            sortingCallback!!.compare(first as SubscriptionVO, second as SubscriptionVO)
        }

        assertEquals(5, list[0].userId)
        assertEquals(4, list[1].userId)
        assertEquals(3, list[2].userId)
        assertEquals(2, list[3].userId)
        assertEquals(1, list[4].userId)
    }
}