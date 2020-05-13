package ru.sudox.design.viewlist

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView

class ViewListContainer : ViewGroup {

    internal var isScrolling = false
        private set

    private var stickyView: View? = null
    private var scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
            if (dy != 0 && isScrolling) {
                showStickyView()
            }
        }

        override fun onScrollStateChanged(view: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                isScrolling = false

                if ((view.adapter as ViewListAdapter<*>).canHideStickyView()) {
                    hideStickyView()
                }
            } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                isScrolling = true
            }
        }
    }

    var viewList: ViewList? = null
        set(value) {
            if (field != null || value == null) {
                removeView(field)
            }

            if (value != null) {
                value.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                value.addOnScrollListener(scrollListener)

                if (stickyView != null) {
                    removeView(stickyView)
                }

                stickyView = (value.adapter as ViewListAdapter<*>).getStickyView(context)

                if (stickyView != null) {
                    addView(stickyView)
                }

                addView(value)
            }

            field = value
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (viewList != null) {
            measureChild(viewList, widthMeasureSpec, heightMeasureSpec)
        }

        if (stickyView != null) {
            measureChild(stickyView, widthMeasureSpec, heightMeasureSpec)
        }

        setMeasuredDimension(viewList?.measuredWidth ?: 0, viewList?.measuredHeight ?: 0)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (stickyView != null && viewList != null) {
            val top = viewList!!.paddingTop
            val left = measuredWidth / 2 - stickyView!!.measuredWidth / 2
            val right = left + stickyView!!.measuredWidth
            val bottom = top + stickyView!!.measuredHeight

            stickyView!!.layout(left, top, right, bottom)
        }

        viewList?.layout(0, 0, measuredWidth, measuredHeight)
    }

    internal fun showStickyView() {
        stickyView!!.alpha = 1F
        checkAndPrepareLayout()
    }

    private fun checkAndPrepareLayout() {
        var firstVisibleProviderView: View? = null
        var firstVisibleProviderViewPosition = Int.MAX_VALUE
        var firstVisibleStickyViewPosition = Int.MAX_VALUE
        var firstVisibleStickyView: View? = null
        val adapter = viewList!!.adapter as ViewListAdapter<*>

        for (i in 0 until viewList!!.childCount) {
            val view = viewList!!.getChildAt(i)
            val position = view.bottom

            if (position <= viewList!!.paddingTop) {
                continue
            }

            if (adapter.isViewCanProvideData(view) && position < firstVisibleProviderViewPosition) {
                firstVisibleProviderViewPosition = position
                firstVisibleProviderView = view
            }

            if (adapter.isViewCanBeSticky(view) && position < firstVisibleStickyViewPosition) {
                firstVisibleStickyViewPosition = position
                firstVisibleStickyView = view
            }

            view.alpha = 1F
        }

        if (firstVisibleProviderView != null) {
            adapter.bindStickyView(stickyView!!, firstVisibleProviderView)
        }

        if (firstVisibleStickyView != null) {
            if (firstVisibleStickyView.top > viewList!!.paddingTop) {
                firstVisibleStickyView.alpha = 1F
                hideStickyView()
            } else {
                firstVisibleStickyView.alpha = 0F
                stickyView!!.alpha = 1F
            }

            val offset = firstVisibleStickyView.bottom - viewList!!.paddingTop

            if (offset > stickyView!!.measuredHeight && offset < stickyView!!.measuredHeight * 2) {
                stickyView!!.translationY = -stickyView!!.measuredHeight * 2F + offset
            } else {
                stickyView!!.translationY = 0F
            }
        } else {
            stickyView!!.translationY = 0F
        }
    }

    internal fun hideStickyView() {

    }
}