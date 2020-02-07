package com.sudox.design.saveableview

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * ViewGroup, имеющая свое дополнительное состояние.
 *
 * V - Тип данной View.
 * S - Тип состояния данной View.
 */
@Suppress("unused")
abstract class SaveableViewGroup<V : ViewGroup, S : SaveableViewState<V>> : ViewGroup {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @Suppress("UNCHECKED_CAST")
    override fun onRestoreInstanceState(parcelable: Parcelable) {
        (parcelable as S).let {
            super.onRestoreInstanceState(it.superState)
            it.writeToView(this as V)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onSaveInstanceState(): Parcelable {
        return createStateInstance(super.onSaveInstanceState()!!).apply {
            readFromView(this as V)
        }
    }

    /**
     * Создает экземпляр состояния сохраняемой View.
     *
     * @param superState Основное состояние View.
     * @return Экземпляр состояния.
     */
    abstract fun createStateInstance(superState: Parcelable): S
}