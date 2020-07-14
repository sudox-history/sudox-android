package ru.sudox.android.core.ui.popup

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.transition.TransitionInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.view.children
import ru.sudox.android.core.ui.R

/**
 * Кастомный PopupMenu, реализованный на основе ListPopupWindow.
 *
 * 1) Поддерживает установку кастомных анимаций
 * 2) Поддерживает обработку состояний в отличии от PopupMenu.
 *
 * @param context Контекст приложения/активности
 */
@SuppressLint("RestrictedApi")
class CustomPopupMenu(
    private val context: Context
) : ListPopupWindow(context) {

    var clickCallback: ((MenuItem) -> (Unit))? = null
    var menu: CustomPopupMenuBuilder? = null

    init {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            val inflater = TransitionInflater.from(context)
            val popup = ListPopupWindow::class.java
                .getDeclaredField("mPopup")
                .apply { isAccessible = true }
                .get(this) as PopupWindow

            popup.enterTransition = inflater.inflateTransition(R.transition.transition_popup_enter)
            popup.exitTransition = inflater.inflateTransition(R.transition.transition_popup_exit)
        }

        // isCheckable отвечает за отображение Ripple-effect'а
        // isChecked отвечает за покраску элементов в основной цвет.
        setOnItemClickListener { _, _, position, _ ->
            val item = menu!!.getItem(position)
            menu!!.selectedItemId = item.itemId

            // Не используем notifyDatasetChanged(), т.к. он отключает анимации
            listView?.children?.forEach {
                (it as CustomPopupMenuItemView).let { view ->
                    view.setActive(view.item!!.isChecked)
                }
            }

            clickCallback!!(item)
            dismiss()
        }
    }

    override fun show() {
        val adapter = CustomPopupMenuAdapter(menu!!, context)

        // Т.к. ListPopupWindow при ширине, равной wrap_content ориентируется на размер View-якоря,
        // то нам нужно самим измерить все View, которые находятся в списке.
        var view: View? = null
        val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val parent = listView ?: FrameLayout(context)
        var maxWidth = 0

        for (i in 0 until adapter.count) {
            view = if (view == null) {
                adapter.getView(i, null, parent)
            } else {
                adapter.getView(i, view, parent)
            }

            view.measure(widthSpec, heightSpec)
            maxWidth = maxWidth.coerceAtLeast(view.measuredWidth)
        }

        setAdapter(adapter)
        setContentWidth(maxWidth)
        super.show()
    }
}