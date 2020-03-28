package com.sudox.messenger.android.layouts

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sudox.design.saveableview.SaveableViewGroup
import com.sudox.messenger.android.layouts.content.ContentLayout

const val LAYOUT_VIEW_ID_KEY = "layout_view_id"
const val FRAME_VIEW_ID_KEY = "frame_view_id"

class AppLayout : SaveableViewGroup<AppLayout, AppLayoutState> {

    val contentLayout = ContentLayout(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        id = View.generateViewId()
    }

    val bottomNavigationView = BottomNavigationView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        id = View.generateViewId()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        addView(contentLayout)
        addView(bottomNavigationView)
    }

    /**
     * Инициализирует данную ViewGroup для отображения в качестве корневого элемента приложения.
     *
     * @param savedInstanceState Сохраненое состояние
     */
    fun init(savedInstanceState: Bundle?) {
        if (savedInstanceState?.containsKey(LAYOUT_VIEW_ID_KEY) == true) {
            id = savedInstanceState.getInt(LAYOUT_VIEW_ID_KEY)

            contentLayout
                    .frameLayout
                    .id = savedInstanceState.getInt(FRAME_VIEW_ID_KEY)
        } else {
            id = View.generateViewId()
        }
    }

    /**
     * Сохраняет необходимые ID в Bundle
     *
     * @param outState Bundle, в который нужно сохранить I
     */
    fun saveIds(outState: Bundle) = outState.let {
        it.putInt(FRAME_VIEW_ID_KEY, contentLayout.frameLayout.id)
        it.putInt(LAYOUT_VIEW_ID_KEY, id)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var contentHeight = MeasureSpec.getSize(heightMeasureSpec)

        measureChild(bottomNavigationView, widthMeasureSpec, heightMeasureSpec)

        if (bottomNavigationView.visibility == View.VISIBLE) {
            contentHeight -= bottomNavigationView.measuredHeight
        }

        contentLayout.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY))

        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (bottomNavigationView.visibility == View.VISIBLE) {
            bottomNavigationView.layout(0, measuredHeight - bottomNavigationView.measuredHeight, bottomNavigationView.measuredWidth, measuredHeight)
        } else {
            bottomNavigationView.layout(0, 0, 0, 0)
        }

        contentLayout.layout(0, 0, contentLayout.measuredWidth, contentLayout.measuredHeight)
    }

    override fun createStateInstance(superState: Parcelable): AppLayoutState {
        return AppLayoutState(superState)
    }
}