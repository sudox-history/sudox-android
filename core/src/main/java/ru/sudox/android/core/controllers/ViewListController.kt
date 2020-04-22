package ru.sudox.android.core.controllers

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.sudox.android.core.CoreController
import ru.sudox.android.core.R
import ru.sudox.design.viewlist.ViewList
import ru.sudox.design.viewlist.ViewListAdapter

abstract class ViewListController<AT : ViewListAdapter<*>> : CoreController() {

    var adapter: AT? = null
        private set

    override fun createView(container: ViewGroup, savedViewState: Bundle?): View {
        return ViewList(activity!!).also {
            it.clipToPadding = false
            it.layoutManager = LinearLayoutManager(activity)
            it.updatePadding(
                    left = activity!!.resources.getDimensionPixelSize(R.dimen.viewlistcontroller_left_padding),
                    right = activity!!.resources.getDimensionPixelSize(R.dimen.viewlistcontroller_right_padding),
                    bottom = activity!!.resources.getDimensionPixelSize(R.dimen.viewlistcontroller_bottom_padding),
                    top = activity!!.resources.getDimensionPixelSize(R.dimen.viewlistcontroller_top_padding)
            )

            adapter = this@ViewListController
                    .getAdapter(it)
                    ?.apply { this.viewList = it }

            it.adapter = adapter
            it.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (it.getCurrentScrollY() > 0) {
                        appBarManager!!.toggleElevation(toggle = true, withAnimation = true)
                    } else {
                        appBarManager!!.toggleElevation(toggle = false, withAnimation = true)
                    }
                }
            })
        }
    }

    override fun isInStartState(): Boolean {
        return (view as ViewList).getCurrentScrollY() == 0
    }

    override fun toStartState() {
        (view as ViewList).smoothScrollToPosition(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter = null
    }

    /**
     * Возвращает адаптер для ViewList (т.е. данного экрана)
     * Ни в коем случее не добавляйте данные в адаптер в данном методе!
     *
     * @param viewList ViewList, с которым нужно связать адаптер
     */
    abstract fun getAdapter(viewList: ViewList): AT?
}