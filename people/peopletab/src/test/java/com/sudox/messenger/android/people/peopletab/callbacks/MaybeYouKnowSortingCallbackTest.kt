package com.sudox.messenger.android.people.peopletab.callbacks

import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.messenger.android.people.peopletab.vos.MaybeYouKnowVO
import edu.emory.mathcs.backport.java.util.Collections
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class MaybeYouKnowSortingCallbackTest : Assert() {

    private var viewListAdapter: ViewListAdapter<*>? = null
    private var sortingCallback: MaybeYouKnowSortingCallback? = null

    @Before
    fun setUp() {
        viewListAdapter = Mockito.mock(ViewListAdapter::class.java)
        sortingCallback = MaybeYouKnowSortingCallback(viewListAdapter!!)
    }

    @Test
    fun testComparing() {
        val list = arrayListOf(
                MaybeYouKnowVO(1, "Test 1", 1L, 1L, 1),
                MaybeYouKnowVO(2, "Test 2", 1L, 1L, 2),
                MaybeYouKnowVO(3, "Test 3", 1L, 1L, 3),
                MaybeYouKnowVO(4, "Test 4", 1L, 1L, 4),
                MaybeYouKnowVO(5, "Test 5", 1L, 1L, 5)
        )

        Collections.sort(list) { first, second ->
            sortingCallback!!.compare(first as MaybeYouKnowVO, second as MaybeYouKnowVO)
        }

        assertEquals(5, list[0].userId)
        assertEquals(4, list[1].userId)
        assertEquals(3, list[2].userId)
        assertEquals(2, list[3].userId)
        assertEquals(1, list[4].userId)
    }
}