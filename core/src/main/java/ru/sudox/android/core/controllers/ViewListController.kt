package ru.sudox.android.core.controllers

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.sudox.android.core.CoreController
import ru.sudox.android.core.R
import ru.sudox.design.viewlist.ViewList
import ru.sudox.design.viewlist.ViewListAdapter

abstract class ViewListController<AT : ViewListAdapter<*>>(
        private val stackFromEnd: Boolean = false,
        private val disableAnimations: Boolean = false
) : CoreController() {

    var adapter: AT? = null
        private set

    override fun createView(container: ViewGroup, savedViewState: Bundle?): View {
        return ViewList(activity!!).also {
            adapter = this@ViewListController.getAdapter(it)?.apply { this.viewList = it }

            if (disableAnimations) {
                it.itemAnimator = null
                it.layoutAnimation = null
            }

            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(activity).apply { stackFromEnd = this@ViewListController.stackFromEnd }
            it.clipToPadding = false
            it.setHasFixedSize(true)

            val leftPadding = activity!!.resources.getDimensionPixelSize(R.dimen.viewlistcontroller_left_padding)
            val rightPadding = activity!!.resources.getDimensionPixelSize(R.dimen.viewlistcontroller_right_padding)
            val bottomPadding = activity!!.resources.getDimensionPixelSize(R.dimen.viewlistcontroller_bottom_padding)
            val topPadding =  activity!!.resources.getDimensionPixelSize(R.dimen.viewlistcontroller_top_padding)

            it.setPadding(leftPadding, topPadding, rightPadding, bottomPadding)
            it.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (it.getCurrentScrollY() > 0) {
                        appBarManager!!.requestElevationToggling(toggle = true, animate = true)
                    } else {
                        appBarManager!!.requestElevationToggling(toggle = false, animate = true)
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