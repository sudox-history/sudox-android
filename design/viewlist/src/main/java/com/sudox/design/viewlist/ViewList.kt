package com.sudox.design.viewlist

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.os.Parcelable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.Interpolator
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.sudox.design.common.getFont
import com.sudox.design.viewlist.decorators.ViewListMarginDecorator
import com.sudox.design.viewlist.decorators.ViewListStickyDecorator

/**
 * Доработанный список на базе RecyclerView
 *
 * В отличии от обычного RecyclerView умеет:
 * 1) Отображать шапки и футеры
 * 2) Переключать секции и сортировки
 * 3) Скрывать секции
 * 4) Выдавать текущую позицию скролла
 */
class ViewList : RecyclerView {

    internal var footerMargin = 0
    internal var footerTextAppearance = 0
    internal var initialPaddingRight = 0
    internal var initialPaddingLeft = 0
    internal var letterPaddingRight = 0
    internal var letterPaddingTop = 0
    internal var letterPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    internal var scrollX = 0
    internal var scrollY = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.viewListStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        context.obtainStyledAttributes(attrs, R.styleable.ViewList, defStyle, 0).use {
            footerMargin = it.getDimensionPixelSize(R.styleable.ViewList_footerMargin, 0)
            footerTextAppearance = it.getResourceIdOrThrow(R.styleable.ViewList_footerTextAppearance)
            letterPaddingRight = it.getDimensionPixelSize(R.styleable.ViewList_letterPaddingRight, 0)
            letterPaddingTop = it.getDimensionPixelSize(R.styleable.ViewList_letterPaddingTop, 0)

            letterPaint.apply {
                typeface = it.getFont(context, R.styleable.ViewList_letterFontFamily)
                textSize = it.getDimensionPixelSize(R.styleable.ViewList_letterTextSize, 0).toFloat()
                color = it.getColorOrThrow(R.styleable.ViewList_letterTextColor)
            }
        }

        addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                scrollX += dx
                scrollY += dy
            }
        })

        addItemDecoration(ViewListMarginDecorator(this))
        addItemDecoration(ViewListStickyDecorator(this))
    }

    /**
     * Выдает текущий скролл по оси X
     * P.S.: Рассчитывается на основе вызова обратной функции
     *
     * @return Текущий скролл по оси X
     */
    fun getCurrentScrollX(): Int {
        return scrollX
    }

    /**
     * Выдает текущий скролл по оси Y
     * P.S.: Рассчитывается на основе вызова обратной функции
     *
     * @return Текущий скролл по оси Y
     */
    fun getCurrentScrollY(): Int {
        return scrollY
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