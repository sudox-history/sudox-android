package ru.sudox.design.viewlist

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.addListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ViewListContainer : ViewGroup {

    internal var isScrolling = false
        private set

    private var stickyView: View? = null
    private var stickyViewHiding = false
    private var isStickyViewIntersected = false
    private var stickyViewSecond: View? = null
    private var stickyViewAnimation: AnimatorSet? = null
    private var scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
            if (dy != 0 && isScrolling) {
                showStickyView()
            }
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

            if (value != null) {
                value.addOnScrollListener(scrollListener)
                value.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> showStickyView() }
                value.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

                if (stickyView != null) {
                    removeView(stickyViewSecond)
                    removeView(stickyView)
                }

                stickyView = (value.adapter as ViewListAdapter<*>).getStickyView(context).apply { alpha = 0F }
                stickyViewSecond = (value.adapter as ViewListAdapter<*>).getStickyView(context).apply { alpha = 0F }

                if (stickyView != null) {
                    addView(stickyViewSecond)
                    addView(stickyView)
                }

                addView(value)
            }

            field = value
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> checkAndPrepareLayout() }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (viewList != null) {
            measureChild(viewList, widthMeasureSpec, heightMeasureSpec)
        }

        if (stickyView != null) {
            measureChild(stickyView, widthMeasureSpec, heightMeasureSpec)
            measureChild(stickyViewSecond, widthMeasureSpec, heightMeasureSpec)
        }

        setMeasuredDimension(viewList?.measuredWidth ?: 0, viewList?.measuredHeight ?: 0)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (stickyView != null && viewList != null) {
            val top = viewList!!.paddingTop
            val firstLeft = measuredWidth / 2 - stickyView!!.measuredWidth / 2
            val firstRight = firstLeft + stickyView!!.measuredWidth
            val bottom = top + stickyView!!.measuredHeight

            stickyView!!.layout(firstLeft, top, firstRight, bottom)

            val secondLeft = measuredWidth / 2 - stickyViewSecond!!.measuredWidth / 2
            val secondRight = secondLeft + stickyViewSecond!!.measuredWidth

            stickyViewSecond!!.layout(secondLeft, top, secondRight, bottom)
        }

        viewList?.layout(0, 0, measuredWidth, measuredHeight)
    }

    internal fun showStickyView() {
        if (!stickyViewHiding) {
            stickyViewAnimation?.cancel()
            stickyViewHiding = true
            stickyViewAnimation = AnimatorSet().apply { duration = 150 }
            stickyViewAnimation!!.playTogether(ObjectAnimator.ofFloat(stickyView!!, View.ALPHA, 1F))
            stickyViewAnimation!!.addListener {
                if (it == stickyViewAnimation) {
                    stickyViewAnimation = null
                }
            }

            stickyViewAnimation!!.start()
        }

        checkAndPrepareLayout()
    }

    internal fun hideStickyView(animated: Boolean) {
        if (stickyViewHiding && !isScrolling && !isStickyViewIntersected) {
            stickyViewHiding = false

            if (animated && (viewList!!.adapter as ViewListAdapter<*>).canHideStickyView()) {
                stickyViewAnimation = AnimatorSet().apply { duration = 150 }
                stickyViewAnimation!!.playTogether(ObjectAnimator.ofFloat(stickyView!!, View.ALPHA, 0F))
                stickyViewAnimation!!.startDelay = 1000
                stickyViewAnimation!!.addListener {
                    if (it == stickyViewAnimation) {
                        stickyViewAnimation = null
                    }
                }

                stickyViewAnimation!!.start()
            } else if (!animated) {
                stickyViewAnimation?.cancel()
                stickyView!!.alpha = 0F
            }
        }
    }

    private fun checkAndPrepareLayout() {
        val manager = viewList!!.layoutManager as? LinearLayoutManager

        // Для списков, счет элементов в которых начинается с конца
        if (manager?.stackFromEnd == true && !viewList!!.canScrollVertically(-1) && !viewList!!.canScrollVertically(1)) {
            stickyViewSecond!!.alpha = 0F
            hideStickyView(false)
            return
        }

        var outboundsView: View? = null
        var firstOutboundsViewPosition = Int.MAX_VALUE
        var firstVisibleProviderView: View? = null
        var firstVisibleProviderViewPosition = Int.MAX_VALUE
        var firstVisibleStickyViewPosition = Int.MAX_VALUE
        var firstVisibleStickyView: View? = null
        val adapter = viewList!!.adapter as ViewListAdapter<*>

        for (i in 0 until viewList!!.childCount) {
            val view = viewList!!.getChildAt(i)
            val position = view.bottom

            if (adapter.isViewCanBeSticky(view) && position < firstVisibleStickyViewPosition) {
                firstVisibleStickyViewPosition = position
                firstVisibleStickyView = view
            }

            if (position > viewList!!.paddingTop) {
                if (adapter.isViewCanProvideData(view) && position < firstVisibleProviderViewPosition) {
                    firstVisibleProviderViewPosition = position
                    firstVisibleProviderView = view
                }
            } else {
                if (position < firstOutboundsViewPosition) {
                    firstOutboundsViewPosition = position
                    outboundsView = view
                }
            }

            view.alpha = 1F
        }

        if (firstVisibleProviderView != null) {
            adapter.bindStickyView(stickyView!!, firstVisibleProviderView)
        }

        isStickyViewIntersected = false

        if (firstVisibleStickyView != null) {
            if (stickyView!!.top == firstVisibleStickyView.top && stickyView!!.bottom == firstVisibleStickyView.bottom) {
                isStickyViewIntersected = true
                firstVisibleStickyView.alpha = 1F
                hideStickyView(false)
            }

            if (firstVisibleStickyView.top > viewList!!.paddingTop) {
                firstVisibleStickyView.alpha = 1F
            } else {
                firstVisibleStickyView.alpha = 0F
                stickyView!!.alpha = 1F
            }

            val offset = firstVisibleStickyView.bottom - viewList!!.paddingTop
            val needTranslation = -stickyView!!.measuredHeight * 2F + offset // Все липкие View должны быть одинаковой высоты

            if (needTranslate(offset, false)) {
                stickyView!!.translationY = needTranslation
                stickyViewSecond!!.alpha = 0F
            } else {
                if (outboundsView != null && needTranslate(offset, true)) {
                    adapter.bindStickyView(stickyViewSecond!!, outboundsView)
                    stickyViewSecond!!.translationY = needTranslation
                    stickyViewSecond!!.alpha = 1F
                } else {
                    stickyViewSecond!!.alpha = 0F
                }

                stickyView!!.translationY = 0F
            }
        } else {
            stickyView!!.translationY = 0F
            stickyViewSecond!!.alpha = 0F
        }
    }

    private fun needTranslate(offset: Int, excludePadding: Boolean): Boolean {
        var newOffset = offset

        if (excludePadding) {
            newOffset += viewList!!.paddingTop
        }

        return newOffset > stickyView!!.measuredHeight && newOffset < stickyView!!.measuredHeight * 2
    }
}