package ru.sudox.android.core.tabs.vos

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import ru.sudox.design.appbar.vos.AppBarLayoutVO
import ru.sudox.design.tablayout.SizeableTabLayout

/**
 * ViewObject для фрагмента с табами
 * Просто добавляет в конец к уже переданному разработчиком VO TabLayout.
 *
 * @param rootVO Основной ViewObject с элементами, которые хотел бы видеть
 * разработчик.
 */
class TabsAppBarLayoutVO(
        val rootVO: AppBarLayoutVO?
) : AppBarLayoutVO {

    var tabLayout: TabLayout? = null

    override fun getViews(context: Context): Array<View>? {
        if (tabLayout == null) {
            tabLayout = SizeableTabLayout(context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
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