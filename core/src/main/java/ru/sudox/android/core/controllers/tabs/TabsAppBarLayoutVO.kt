package ru.sudox.android.core.controllers.tabs

import android.content.Context
import android.view.View
import android.view.ViewGroup
import ru.sudox.design.appbar.vos.AppBarLayoutVO
import ru.sudox.design.tablayout.SizeableTabLayout

class TabsAppBarLayoutVO(
        val context: Context,
        val rootVO: AppBarLayoutVO?
) : AppBarLayoutVO {

    var tabLayout = SizeableTabLayout(context).apply {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun getViews(context: Context): Array<View>? {
        val rootViews = rootVO?.getViews(context)
        val tabLayoutArray = arrayOf<View>(tabLayout)

        return if (rootViews != null) {
            rootViews + tabLayoutArray
        } else {
            tabLayoutArray
        }
    }
}