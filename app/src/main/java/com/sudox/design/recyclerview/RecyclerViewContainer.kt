package com.sudox.design.recyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ProgressBar

class RecyclerViewContainer : FrameLayout {

    var recyclerView: RecyclerView = RecyclerView(context)
    var loadingSpinner: ProgressBar = ProgressBar(context)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        addView(recyclerView, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        addView(loadingSpinner, LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER))

        notifyInitialLoadingStart()
    }

    fun notifyInitialLoadingStart(){
        recyclerView.visibility = View.INVISIBLE
        loadingSpinner.visibility = View.VISIBLE
    }

    fun notifyInitialLoadingDone() {
        recyclerView.visibility = View.VISIBLE
        loadingSpinner.visibility = View.GONE
    }

    fun notifyListEmpty() {
        recyclerView.visibility = View.INVISIBLE
        loadingSpinner.visibility = View.GONE

        // TODO: Show illustration
    }
}