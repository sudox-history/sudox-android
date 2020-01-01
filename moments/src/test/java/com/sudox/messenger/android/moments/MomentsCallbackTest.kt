package com.sudox.messenger.android.moments

import android.app.Activity
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.SortedList
import com.sudox.messenger.android.moments.vos.MomentVO
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.Robolectric

@RunWith(MomentsTestRunner::class)
class MomentsCallbackTest : Assert() {

    private var drawable: Drawable? = null
    private var callback: MomentsCallback? = null
    private var adapter: MomentsAdapter? = null

    @Before
    fun setUp() {
        adapter = MomentsAdapter(Robolectric.buildActivity(Activity::class.java).get())
        drawable = Mockito.mock(Drawable::class.java)
        callback = MomentsCallback(adapter!!)
    }

    @Test
    fun testSorting() {
        val list = SortedList(MomentVO::class.java, callback!!)
        val first = MomentVO(true, isFullyViewed = false, publishTime = 2L, userName = "1", userPhoto = drawable!!)
        val second = MomentVO(true, isFullyViewed = false, publishTime = 1L, userName = "2", userPhoto = drawable!!)
        val third = MomentVO(true, isFullyViewed = true, publishTime = 3L, userName = "3", userPhoto = drawable!!)
        val fourth = MomentVO(true, isFullyViewed = true, publishTime = 2L, userName = "4", userPhoto = drawable!!)
        val fifth = MomentVO(true, isFullyViewed = true, publishTime = 1L, userName = "5", userPhoto = drawable!!)

        list.add(fifth)
        list.add(third)
        list.add(first)
        list.add(fourth)
        list.add(second)

        assertEquals(first, list[0])
        assertEquals(second, list[1])
        assertEquals(third, list[2])
        assertEquals(fourth, list[3])
        assertEquals(fifth, list[4])
    }
}