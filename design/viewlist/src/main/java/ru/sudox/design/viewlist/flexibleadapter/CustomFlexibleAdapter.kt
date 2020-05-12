package ru.sudox.design.viewlist.flexibleadapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter

internal val GET_VIEW_TYPE_INSTANCE_INVOKE = CustomFlexibleAdapter::class.java
        .getDeclaredMethod("getViewTypeInstance", Int::class.java)
        .apply { isAccessible = true }

internal val AUTO_MAP_FIELD = CustomFlexibleAdapter::class.java
        .getDeclaredField("autoMap")
        .apply { isAccessible = true }

abstract class CustomFlexibleAdapter<T : CustomFlexible<*>?> : FlexibleAdapter<T> {

    @Suppress("unused")
    constructor(items: MutableList<T>?) : super(items)
    @Suppress("unused")
    constructor(items: MutableList<T>?, listeners: Any?) : super(items, listeners)
    @Suppress("unused")
    constructor(items: MutableList<T>?, listeners: Any?, stableIds: Boolean) : super(items, listeners, stableIds)

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val item = GET_VIEW_TYPE_INSTANCE_INVOKE.invoke(this, viewType) as T

        if (item == null || !AUTO_MAP_FIELD.getBoolean(this)) {
            throw IllegalStateException("ViewType instance not found for viewType $viewType. You should implement the AutoMap properly.")
        }

        return item.createViewHolder(parent.context, this)
    }
}