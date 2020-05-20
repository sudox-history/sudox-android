package ru.sudox.android.messages.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ru.sudox.design.viewlist.ViewList
import ru.sudox.design.viewlist.ViewListAdapter
import ru.sudox.design.viewlist.ViewListContainer

class MessageScreenLayout : ViewGroup {

    private val viewListContainer = ViewListContainer(context).apply {
        this@MessageScreenLayout.addView(this)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY)
        val widthSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY)

        measureChild(viewListContainer, widthSpec, heightSpec)
        setMeasuredDimension(viewListContainer.measuredWidth, viewListContainer.measuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        viewListContainer.layout(0, 0, measuredWidth, measuredHeight)
    }

    fun setAdapter(adapter: ViewListAdapter<*>) {
        viewListContainer.viewList = ViewList(context).apply {
            adapter.viewList = this

            this.adapter = adapter
            this.itemAnimator = null
            this.layoutAnimation = null
            this.layoutManager = LinearLayoutManager(context).apply { stackFromEnd = true }
        }
    }
}