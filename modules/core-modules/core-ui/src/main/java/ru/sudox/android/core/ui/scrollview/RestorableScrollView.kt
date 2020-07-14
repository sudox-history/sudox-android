package ru.sudox.android.core.ui.scrollview

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

/**
 * ScrollView, уведомляющая о восстановлении состояния.
 */
class RestorableScrollView : NestedScrollView {

    var restoreStateEventCallback: (() -> (Unit))? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        restoreStateEventCallback?.invoke()
    }
}