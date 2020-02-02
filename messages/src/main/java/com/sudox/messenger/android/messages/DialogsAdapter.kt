package com.sudox.messenger.android.messages

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.sudox.messenger.android.messages.views.DialogItemView
import com.sudox.messenger.android.messages.vos.DialogItemViewVO

class DialogsAdapter(val context: Context) : RecyclerView.Adapter<DialogsAdapter.ViewHolder>() {

    val dialogs = SortedList<DialogItemViewVO>(DialogItemViewVO::class.java, DialogsCallback(this))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogsAdapter.ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: DialogsAdapter.ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class ViewHolder(val view: DialogItemView) : RecyclerView.ViewHolder(view)
}