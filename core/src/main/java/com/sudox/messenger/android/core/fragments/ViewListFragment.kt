package com.sudox.messenger.android.core.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.R

const val VIEW_LIST_ID_KEY = "view_list_id"

/**
 * Фрагмент с контентом, содержащимся во ViewList
 *
 * Отвечает за:
 * 1) Отображение ViewList и задание ему параметров
 * 2) Сохранение состояния ViewList
 *
 * AT - тип адаптера
 */
abstract class ViewListFragment<AT : ViewListAdapter<*>> : CoreFragment() {

    private var viewList: ViewList? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewList = ViewList(context!!).also { viewList ->
            viewList.layoutManager = LinearLayoutManager(context)
            viewList.updatePadding(
                    left = context!!.resources.getDimensionPixelSize(R.dimen.viewlistfragment_left_padding),
                    right = context!!.resources.getDimensionPixelSize(R.dimen.viewlistfragment_right_padding),
                    bottom = context!!.resources.getDimensionPixelSize(R.dimen.viewlistfragment_bottom_padding),
                    top = context!!.resources.getDimensionPixelSize(R.dimen.viewlistfragment_top_padding)
            )

            viewList.clipToPadding = false
            viewList.id = savedInstanceState?.getInt(VIEW_LIST_ID_KEY, View.generateViewId()) ?: View.generateViewId()
            viewList.adapter = this@ViewListFragment.getAdapter(viewList)?.apply {
                this.viewList = viewList
            }
        }

        return viewList
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(VIEW_LIST_ID_KEY, viewList!!.id)
    }

    /**
     * Возвращает адаптер для ViewList (т.е. данного экрана)
     *
     * @param viewList ViewList, с которым нужно связать адаптер
     */
    abstract fun getAdapter(viewList: ViewList): AT?
}