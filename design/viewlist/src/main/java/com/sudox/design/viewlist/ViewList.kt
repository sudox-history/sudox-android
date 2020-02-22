package com.sudox.design.viewlist

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.Interpolator
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

/**
 * Доработанный список на базе RecyclerView
 *
 * В отличии от обычного RecyclerView умеет:
 * 1) Отображать шапки и футеры
 * 2) Переключать секции и сортировки
 * 3) Скрывать секции
 */
class ViewList : RecyclerView {

    internal var footerTextAppearance = 0
    internal var initialPaddingRight = 0
    internal var initialPaddingLeft = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.viewListStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        context.obtainStyledAttributes(attrs, R.styleable.ViewList, defStyle, 0).use {
            footerTextAppearance = it.getResourceIdOrThrow(R.styleable.ViewList_footerTextAppearance)
        }
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        if ((layoutManager as? LinearLayoutManager)?.orientation == VERTICAL) {
            if (left > 0) {
                initialPaddingLeft = left
            }

            if (right > 0) {
                initialPaddingRight = right
            }

            super.setPadding(0, top, 0, bottom)
        } else {
            super.setPadding(left, top, right, bottom)
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)

        if (adapter != null && adapter is ViewListAdapter<*>) {
            addItemDecoration(ViewListDecorator(adapter, this))
        } else if (itemDecorationCount > 0) {
            removeItemDecoration(getItemDecorationAt(0))
        }
    }

    override fun scrollToPosition(position: Int) {
        if (isFocusable) {
            super.scrollToPosition(position)
        }
    }

    override fun smoothScrollToPosition(position: Int) {
        if (isFocusable) {
            super.smoothScrollToPosition(position)
        }
    }

    override fun scrollBy(x: Int, y: Int) {
        if (isFocusable) {
            super.scrollBy(x, y)
        }
    }

    override fun smoothScrollBy(dx: Int, dy: Int, interpolator: Interpolator?) {
        if (isFocusable) {
            super.smoothScrollBy(dx, dy, interpolator)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent?): Boolean {
        if (!isFocusable) {
            return false
        }

        return super.onTouchEvent(e)
    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        if (!isFocusable) {
            return false
        }

        return super.onInterceptTouchEvent(e)
    }

    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        if (!isFocusable) {
            return false
        }

        return super.onGenericMotionEvent(event)
    }

    override fun onSaveInstanceState(): Parcelable? {
        return ViewListState(super.onSaveInstanceState()!!).apply {
            readFromView(this@ViewList)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        (state as ViewListState).apply {
            super.onRestoreInstanceState(superState)
            writeToView(this@ViewList)
        }
    }
}