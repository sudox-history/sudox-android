package ru.sudox.design.viewlist

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.addListener
import androidx.recyclerview.widget.RecyclerView

class ViewListContainer : ViewGroup {

    internal var isMinChildNotSticky = false
        private set

    internal var isScrolling = false
        private set

    var marginBetweenTopAndStickyView = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    private var stickyView: View? = null
    private var isStickyFloating = false
    private var stickyViewAnimation: AnimatorSet? = null
    private var scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
            if (dy != 0 && isScrolling && !isMinChildNotSticky) {
                showStickyView()
            }

            checkAndPrepareLayout()
        }

        override fun onScrollStateChanged(view: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                isScrolling = false
                hideStickyView(true)
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

            field = value

            if (value != null) {
                addView(value)

                value.addOnScrollListener(scrollListener)
                value.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

                if (stickyView != null) {
                    removeView(stickyView)
                }

                stickyView = (value.adapter as ViewListAdapter<*>).getStickyView(context)

                if (stickyView != null) {
                    stickyView!!.alpha = 0F
                    addView(stickyView)
                }
            }
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
            val top = marginBetweenTopAndStickyView
            val firstLeft = measuredWidth / 2 - stickyView!!.measuredWidth / 2
            val firstRight = firstLeft + stickyView!!.measuredWidth
            val bottom = top + stickyView!!.measuredHeight

            stickyView!!.layout(firstLeft, top, firstRight, bottom)
        }

        viewList?.layout(0, 0, measuredWidth, measuredHeight)
    }

    internal fun showStickyView() {
        if (stickyView!!.tag == null) {
            stickyViewAnimation?.cancel()
            stickyView!!.tag = 1

            stickyViewAnimation = AnimatorSet().apply { duration = 150 }
            stickyViewAnimation!!.playTogether(ObjectAnimator.ofFloat(stickyView!!, View.ALPHA, 1F))
            stickyViewAnimation!!.addListener(onEnd = {
                if (it == stickyViewAnimation) {
                    stickyViewAnimation = null
                }
            })

            stickyViewAnimation!!.start()
        }
    }

    internal fun hideStickyView(animated: Boolean) {
        if (stickyView!!.tag != null && !isStickyFloating && (!isScrolling || isMinChildNotSticky)) {
            stickyView!!.tag = null

            if (animated) {
                stickyViewAnimation = AnimatorSet().apply {
                    startDelay = 1000
                    duration = 150
                }

                stickyViewAnimation!!.playTogether(ObjectAnimator.ofFloat(stickyView!!, View.ALPHA, 0F))
                stickyViewAnimation!!.addListener(onEnd = {
                    if (it == stickyViewAnimation) {
                        stickyViewAnimation = null
                    }
                })

                stickyViewAnimation!!.start()
            } else {
                stickyViewAnimation?.cancel()
                stickyViewAnimation = null
                stickyView!!.alpha = 0F
            }
        }
    }

    internal fun checkAndPrepareLayout() {
        var minChildView: View? = null
        var minChildViewPosition = Int.MAX_VALUE
        var firstVisibleProviderView: View? = null
        var firstVisibleProviderViewPosition = Int.MAX_VALUE
        var firstVisibleStickyViewPosition = Int.MAX_VALUE
        var firstVisibleStickyView: View? = null
        val adapter = viewList!!.adapter as ViewListAdapter<*>

        for (i in 0 until viewList!!.childCount) {
            val view = viewList!!.getChildAt(i)
            val position = view.bottom

            if (position >= marginBetweenTopAndStickyView) {
                if (position < minChildViewPosition) {
                    minChildViewPosition = position
                    minChildView = view
                }

                if (adapter.isViewCanProvideData(view) && position < firstVisibleProviderViewPosition) {
                    firstVisibleProviderViewPosition = position
                    firstVisibleProviderView = view
                }

                if (adapter.isViewCanBeSticky(view) && position < firstVisibleStickyViewPosition) {
                    firstVisibleStickyViewPosition = position
                    firstVisibleStickyView = view
                    view.alpha = 1F
                }
            }
        }

        if (firstVisibleProviderView != null) {
            adapter.bindStickyView(stickyView!!, firstVisibleProviderView)
        }

        isStickyFloating = false
        isMinChildNotSticky = !(adapter.isViewCanBeSticky(minChildView!!) || adapter.isViewCanProvideData(minChildView))

        if (firstVisibleStickyView != null) {
            if (firstVisibleStickyView.top > marginBetweenTopAndStickyView || isMinChildNotSticky) {
                firstVisibleStickyView.alpha = 1F
                hideStickyView(!isMinChildNotSticky)
            } else {
                stickyView!!.tag = 1
                stickyView!!.alpha = 1F
                firstVisibleStickyView.alpha = 0F
                stickyViewAnimation?.cancel()
                stickyViewAnimation = null
                isStickyFloating = true
            }

            val offset = firstVisibleStickyView.bottom - marginBetweenTopAndStickyView

            if (offset > stickyView!!.measuredHeight && offset < stickyView!!.measuredHeight * 2) {
                stickyView!!.translationY = -stickyView!!.measuredHeight * 2F + offset
            } else {
                stickyView!!.translationY = 0F
            }
        } else {
            hideStickyView(true)
            stickyView!!.translationY = 0F
        }
    }
}