package ru.sudox.design.popup

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.sudox.design.popup.vos.PopupItemVO
import kotlin.math.max

/**
 * Popup-меню, построенное на базе RecyclerView
 *
 * @param context Контекст приложения/активности
 * @param itemClickedCallback Кэллбэк клика по элементу
 * @param hideAfterSelection Скрывать после выбора элемента?
 * @param items VO элементов меню
 */
class ListPopupWindow(
        val context: Context,
        val items: List<PopupItemVO<*>>,
        val hideAfterSelection: Boolean,
        val itemClickedCallback: (PopupItemVO<*>) -> (Unit)
) : PopupWindow(context, null, R.attr.listPopupWindowStyle) {

    private var marginBetweenPopupAndAnchor = context
            .resources
            .getDimensionPixelSize(R.dimen.listpopupwindow_margin_between_popup_and_anchor)

    private val adapter = ListPopupAdapter(this, items)
    private var viewList = RecyclerView(context)

    init {
        isOutsideTouchable = true

        // Костыль для определения элемента с максимальной шириной
        // Размер иконки у всех VO одинаковый
        val itemHolder = adapter.createViewHolder(viewList, 0)
        val measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

        for (item in items) {
            itemHolder.view.let {
                it.vo = item
                it.measure(measureSpec, measureSpec)
                adapter.maximumItemWidth = max(it.measuredWidth, adapter.maximumItemWidth)
            }
        }

        contentView = viewList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ListPopupWindow.adapter
        }
    }

    /**
     * Правильно расчитывает положение окна на оси X и открывает его
     *
     * @param anchor View-маяк
     * @param gravity Где должен отображаться элемент относительно элемента маяка
     */
    fun showAsDropDown(anchor: View, gravity: Int) {
        val rootView = anchor.rootView
        val size = IntArray(2).apply { anchor.getLocationInWindow(this) }
        val y = size[1] + anchor.measuredHeight + marginBetweenPopupAndAnchor

        showAtLocation(rootView, Gravity.NO_GRAVITY, if (gravity == Gravity.END) {
            size[0] - adapter.maximumItemWidth + anchor.measuredWidth
        } else {
            size[0]
        }, y)
    }

    internal fun invokeItemClickedEvent(clickedItem: PopupItemVO<*>) {
        if (hideAfterSelection) {
            dismiss()
        }

        itemClickedCallback(clickedItem)
    }
}