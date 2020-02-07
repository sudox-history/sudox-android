package com.sudox.design.saveableview

import android.os.Parcel
import android.os.Parcelable
import android.view.View

/**
 * Состояние сохраняемой View
 * Внимание! Класс работает только с View, которые наследуются от SaveableView.
 * Не забудьте переопределить метод writeToParcel() и конструктор (source: Parcel)
 *
 * V - View, которая может быть сохранена данным состоянием.
 */
@Suppress("LeakingThis", "unused")
abstract class SaveableViewState<V : View> : View.BaseSavedState {

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source)

    /**
     * Записывает данные из View в состояние.
     *
     * @param view View, из которой нужно считать данные
     */
    abstract fun readFromView(view: V)

    /**
     * Записывает данные из состояния во View.
     *
     * @param view View, в которую нужно записать данные
     */
    abstract fun writeToView(view: V)
}