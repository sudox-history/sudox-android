package com.sudox.design.widgets.navbar

import android.view.View
import android.view.ViewGroup
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.novoda.espresso.ViewCreator
import com.novoda.espresso.ViewTestRule
import com.sudox.design.R
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class NavigationBarTest : Assert() {

    @Rule
    @JvmField
    var viewTestRule = ViewTestRule<NavigationBar>(ViewCreator<NavigationBar> { context, _ ->
        Thread.sleep(15000)

        return@ViewCreator NavigationBar(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.resources.getDimensionPixelSize(R.dimen.navigationbar_height))
        }
    })

    @Test
    fun test() {
        viewTestRule.runOnMainSynchronously {
            it.setTitleText("Новый контакт")
            it.buttonStart!!.visibility = View.VISIBLE
            it.buttonStart!!.isClickable = true
            it.buttonStart!!.setOnClickListener {  }
            it.buttonStart!!.setIconDrawableRes(R.drawable.ic_arrow_back_black_24dp)
            it.buttonsEnd[0]!!.visibility = View.VISIBLE
            it.buttonsEnd[0]!!.setText("Добавить")
            it.buttonsEnd[0]!!.isClickable = true
        }

        Thread.sleep(50000)
    }
}