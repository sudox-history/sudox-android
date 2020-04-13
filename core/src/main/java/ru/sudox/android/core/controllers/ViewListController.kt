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
        return ViewList(activity!!).apply {
            updatePadding(
                    left = context!!.resources.getDimensionPixelSize(R.dimen.viewlistcontroller_left_padding),
                    right = context!!.resources.getDimensionPixelSize(R.dimen.viewlistcontroller_right_padding),
                    bottom = context!!.resources.getDimensionPixelSize(R.dimen.viewlistcontroller_bottom_padding),
                    top = context!!.resources.getDimensionPixelSize(R.dimen.viewlistcontroller_top_padding)
            )

            clipToPadding = false
            layoutManager = LinearLayoutManager(context)
            adapter = getAdapter(this)?.also {
                it.viewList = this
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