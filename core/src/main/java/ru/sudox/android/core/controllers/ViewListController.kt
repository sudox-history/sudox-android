package ru.sudox.android.core.controllers

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import ru.sudox.android.core.CoreController
import ru.sudox.android.core.R
import ru.sudox.design.viewlist.ViewList
import ru.sudox.design.viewlist.ViewListAdapter

abstract class ViewListController<AT : ViewListAdapter<*>> : CoreController() {

    override fun createView(container: ViewGroup, savedViewState: Bundle?): View {
        return ViewList(activity!!).also { viewList ->
            viewList.layoutManager = LinearLayoutManager(activity)
            viewList.updatePadding(
                    left = activity!!.resources.getDimensionPixelSize(R.dimen.viewlistcontroller_left_padding),
                    right = activity!!.resources.getDimensionPixelSize(R.dimen.viewlistcontroller_right_padding),
                    bottom = activity!!.resources.getDimensionPixelSize(R.dimen.viewlistcontroller_bottom_padding),
                    top = activity!!.resources.getDimensionPixelSize(R.dimen.viewlistcontroller_top_padding)
            )

            viewList.clipToPadding = false
            viewList.adapter = this@ViewListController.getAdapter(viewList)?.apply {
                this.viewList = viewList
            }
        }
    }

    /**
     * Возвращает адаптер для ViewList (т.е. данного экрана)
     * Ни в коем случее не добавляйте данные в адаптер в данном методе!
     *
     * @param viewList ViewList, с которым нужно связать адаптер
     */
    abstract fun getAdapter(viewList: ViewList): AT?
}