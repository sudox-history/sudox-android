package com.sudox.messenger.android.moments

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.lists.sortedList.decorations.MarginItemDecoration

fun createMomentsRecyclerView(context: Context, moments: ArrayList<MomentVO>): RecyclerView {
    val horizontalMargin = context.resources.getDimensionPixelSize(R.dimen.momentadapter_horizontal_margin)
    val verticalMargin = context.resources.getDimensionPixelSize(R.dimen.momentadapter_vertical_margim)

    return RecyclerView(context).apply {
        addItemDecoration(MarginItemDecoration(verticalMargin, horizontalMargin))

        clipToPadding = false
        layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        adapter = MomentsAdapter(moments, context)
    }
}