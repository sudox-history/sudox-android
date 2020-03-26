package com.sudox.messenger.android.core.tabs.vos

import android.content.Context
import android.view.View
import com.sudox.design.appbar.vos.AppBarLayoutVO
import com.sudox.design.tablayout.TabLayout

class TabsChildAppBarLayoutVO(
        val rootVO: AppBarLayoutVO?
) : AppBarLayoutVO {

    var tabLayout: TabLayout? = null

    override fun getViews(context: Context): Array<View>? {
        if (tabLayout == null) {
            tabLayout = TabLayout(context)
        }

        val rootViews = rootVO?.getViews(context)
        val tabLayoutArray = arrayOf<View>(tabLayout!!)

        return if (rootViews != null) {
            rootViews + tabLayoutArray
        } else {
            tabLayoutArray
        }
    }
}