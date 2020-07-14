package ru.sudox.android.core.ui.popup

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.widget.BaseAdapter
import ru.sudox.android.core.ui.R

/**
 * Адаптер для списка с пунктами меню.
 *
 * @param menu Меню, которое будет отображено
 * @param context Контекст приложения/активности
 */
@SuppressLint("RestrictedApi")
class CustomPopupMenuAdapter(
    private val menu: Menu,
    context: Context
) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return ((convertView ?: inflater.inflate(R.layout.item_popupmenu, parent, false)) as CustomPopupMenuItemView).also {
            it.item = getItem(position)
        }
    }

    override fun getItem(position: Int): MenuItem = menu.getItem(position)
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getCount(): Int = menu.size()
}