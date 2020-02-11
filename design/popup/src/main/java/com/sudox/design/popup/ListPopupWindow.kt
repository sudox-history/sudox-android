package com.sudox.design.popup

import android.content.Context
import android.view.View
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.popup.vos.PopupItemVO
import kotlin.math.max

/**
 * Popup-меню, построенное на базе RecyclerView
 *
 * @param context Контекст приложения/активности
 * @param items VO элементов меню
 */
class ListPopupWindow(
        context: Context,
        items: List<PopupItemVO<*>>
) : PopupWindow(context, null, R.attr.listPopupWindowStyle) {

    private var viewList = RecyclerView(context)
    private val adapter = ListPopupAdapter(context, items)

    init {
        isOutsideTouchable = true

        // Костыль для определения элемента с максимальной шириной
        // Размер иконки у всех VO одинаковый
        val itemHolder = adapter.createViewHolder(viewList, 0)
        val measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val voWithLongestTitle = items.maxBy { it.title }!!

        itemHolder.view.let {
            it.vo = voWithLongestTitle
            it.measure(measureSpec, measureSpec)
            adapter.maximumItemWidth = max(it.measuredWidth, adapter.maximumItemWidth)
        }

        contentView = viewList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ListPopupWindow.adapter
        }
    }
}