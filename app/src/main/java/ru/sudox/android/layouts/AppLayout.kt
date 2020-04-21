package ru.sudox.android.layouts

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.sudox.android.layouts.content.ContentLayout

const val APPBARLAYOUT_ID_KEY = "appbarlayout_id"
const val FRAMELAYOUT_ID_KEY = "framelayout_id"

/**
 * Layout, содержащий ContentLayout и BottomNavigationView.
 * Отвечает за отображение содержимого в приложении.
 *
 * NB! ID должны восстанавливаться в ручном режиме, т.е. в onCreate().
 */
class AppLayout : ViewGroup {

    val contentLayout = ContentLayout(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    val bottomNavigationView = BottomNavigationView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        addView(contentLayout)
        addView(bottomNavigationView)

        systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }

    /**
     * Инициализирует данную ViewGroup для отображения в качестве корневого элемента приложения.
     *
     * @param savedInstanceState Сохраненое состояние
     */
    fun init(savedInstanceState: Bundle?) {
        if (savedInstanceState?.containsKey(APPBARLAYOUT_ID_KEY) == true) {
            contentLayout.appBarLayout.id = savedInstanceState.getInt(APPBARLAYOUT_ID_KEY)
            contentLayout.frameLayout.id = savedInstanceState.getInt(FRAMELAYOUT_ID_KEY)
        } else {
            contentLayout.appBarLayout.id = View.generateViewId()
            contentLayout.frameLayout.id = View.generateViewId()
        }
    }

    /**
     * Сохраняет необходимые ID в Bundle
     *
     * @param outState Bundle, в который нужно сохранить I
     */
    fun saveIds(outState: Bundle) = outState.let {
        it.putInt(APPBARLAYOUT_ID_KEY, contentLayout.appBarLayout.id)
        it.putInt(FRAMELAYOUT_ID_KEY, contentLayout.frameLayout.id)
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
}