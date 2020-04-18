package ru.sudox.android.layouts

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.bluelinelabs.conductor.ChangeHandlerFrameLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.sudox.design.appbar.AppBar
import ru.sudox.design.appbar.AppBarLayout

private const val LAYOUT_VIEW_ID_KEY = "layout_view_id"
private const val APPBARLAYOUT_ID_KEY = "appbarlayout_id"
private const val FRAMELAYOUT_ID_KEY = "framelayout_id"

/**
 * Layout, содержащий AppBarLayout и FrameLayout.
 * Отвечает за отображение тулбара и контента в приложении.
 */
class AppLayout : CoordinatorLayout {

    @Suppress("unused")
    val appBarLayout = AppBarLayout(context).apply {
        id = View.generateViewId()
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        appBar = AppBar(context)

        this@AppLayout.addView(this)
    }

    val frameLayout = ChangeHandlerFrameLayout(context).apply {
        id = View.generateViewId()
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
            behavior = AppBehavior()
        }

        this@AppLayout.addView(this)
    }

    val navigationView = BottomNavigationView(context).apply {
        id = View.generateViewId()
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.BOTTOM
        }

        this@AppLayout.addView(this)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * Инициализирует данную ViewGroup для отображения в качестве корневого элемента приложения.
     *
     * @param savedInstanceState Сохраненое состояние
     */
    fun init(savedInstanceState: Bundle?) {
        if (savedInstanceState?.containsKey(LAYOUT_VIEW_ID_KEY) == true) {
            id = savedInstanceState.getInt(LAYOUT_VIEW_ID_KEY)
            appBarLayout.id = savedInstanceState.getInt(APPBARLAYOUT_ID_KEY)
            frameLayout.id = savedInstanceState.getInt(FRAMELAYOUT_ID_KEY)
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
        it.putInt(APPBARLAYOUT_ID_KEY, appBarLayout.id)
        it.putInt(FRAMELAYOUT_ID_KEY, frameLayout.id)
        it.putInt(LAYOUT_VIEW_ID_KEY, id)
    }
}