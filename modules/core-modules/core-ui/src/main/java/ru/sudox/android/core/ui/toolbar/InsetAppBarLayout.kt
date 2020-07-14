package ru.sudox.android.core.ui.toolbar

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.appbar.AppBarLayout
import ru.sudox.android.core.ui.applyInserts

/**
 * AppBarLayout с поддержкой Edge-to-edge
 */
class InsetAppBarLayout : AppBarLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        applyInserts(top = true, bottom = false)
    }
}