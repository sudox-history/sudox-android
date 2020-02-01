package com.sudox.messenger.android.moments

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.lists.decorators.MarginItemDecoration

fun createMomentsRecyclerView(context: Context): RecyclerView {
    val horizontalMargin = context.resources.getDimensionPixelSize(R.dimen.momentadapter_horizontal_margin)
    val verticalMargin = context.resources.getDimensionPixelSize(R.dimen.momentadapter_vertical_margim)

    return RecyclerView(context).apply {
        addItemDecoration(MarginItemDecoration(verticalMargin, horizontalMargin))

        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        adapter = MomentsAdapter(context)
    }
}