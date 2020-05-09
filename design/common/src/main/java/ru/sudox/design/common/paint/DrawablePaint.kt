package ru.sudox.design.common.paint

import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.annotation.StyleableRes
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.graphics.drawable.updateBounds

/**
 * Контроллер Paint'а для отрисовки Drawable
 *
 * 1) Экономит ОЗУ за счет минимального копирования переданной View
 * 2) Умеет изменять размер Drawable
 * 3) Умеет изменять оттенок Drawable
 */
class DrawablePaint {

    private var isTintSet = false
    private var isSizeSet = false
    private var isAlphaSet = false
    private var isDrawableMutated = false

    var tintColor = 0
        set(value) {
            isTintSet = true
            mutateDrawableIfNeed()
            drawable?.setTint(value)

            field = value
        }

    var width = 0
        set(value) {
            field = value

            if (drawable != null && drawable!!.intrinsicWidth != value) {
                isSizeSet = true
                mutateDrawableIfNeed()
                drawable!!.updateBounds(right = value)
            }
        }

    var height = 0
        set(value) {
            field = value

            if (drawable != null && drawable!!.intrinsicHeight != value) {
                isSizeSet = true
                mutateDrawableIfNeed()
                drawable!!.updateBounds(bottom = value)
            }
        }

    var alpha = 0
        set(value) {
            field = value

            if (drawable?.alpha != value) {
                isAlphaSet = true
                mutateDrawableIfNeed()
                drawable!!.alpha = value
            }
        }

    var drawable: Drawable? = null
        set(value) {
            field = value

            if (value != null) {
                if (isTintSet) {
                    tintColor = tintColor
                }

                if (isSizeSet) {
                    height = height
                    width = width
                }

                if (isAlphaSet) {
                    alpha = alpha
                }
            }
        }

    constructor()
    constructor(drawable: Drawable) {
        this.drawable = drawable
    }

    /**
     * Отрисовывает Drawable на Canvas.
     * P.S.: Если Drawable = null, то ничего отрисовано не будет
     *
     * @param canvas Canvas, на который нужно отрисовать Drawable
     */
    fun draw(canvas: Canvas) {
        drawable?.draw(canvas)
    }

    /**
     * Считывает параметры из TypedArray
     *
     * @param typedArray TypedArray, с которого будет производится чтение
     * @param drawableRes ID параметра, указывающего на Drawable (0 - если нет)
     * @param heightRes ID параметра, указывающего на высоту Drawable (0 - если нет)
     * @param widthRes ID параметра, указывающего на ширину Drawable (0 - если нет)
     * @param tintColorRes ID параметра, указывающего на оттенок Drawable (0 - если нет)
     */
    fun readFromTypedArray(
            typedArray: TypedArray,
            @StyleableRes drawableRes: Int = 0,
            @StyleableRes heightRes: Int = 0,
            @StyleableRes widthRes: Int = 0,
            @StyleableRes tintColorRes: Int = 0
    ) {
        if (drawableRes != 0) {
            drawable = typedArray.getDrawableOrThrow(drawableRes)
        }

        if (heightRes != 0) {
            height = typedArray.getDimensionPixelSizeOrThrow(heightRes)
        }

        if (widthRes != 0) {
            width = typedArray.getDimensionPixelSizeOrThrow(widthRes)
        }

        if (tintColorRes != 0) {
            tintColor = typedArray.getColorOrThrow(tintColorRes)
        }
    }

    fun mutateDrawableIfNeed() {
        if (!isDrawableMutated && drawable != null) {
            isDrawableMutated = true
            drawable = drawable?.mutate()
        }
    }
}