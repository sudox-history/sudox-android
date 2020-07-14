package ru.sudox.android.core.ui.lists

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import ru.sudox.android.core.ui.lists.holder.LoaderViewHolder
import ru.sudox.simplelists.BasicListHolder
import ru.sudox.simplelists.loadable.LoadableListAdapter

/**
 * Адаптер для списка экрана.
 */
abstract class ScreenListAdapter : LoadableListAdapter() {

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.layoutAnimation = null
        recyclerView.itemAnimator = null
    }

    override fun createLoaderViewHolder(context: Context, inflater: LayoutInflater, parent: ViewGroup): BasicListHolder<*> =
        LoaderViewHolder(ProgressBar(context))
}