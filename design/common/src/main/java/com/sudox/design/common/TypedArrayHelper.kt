package com.sudox.design.common

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.content.res.TypedArray

/**
 * Получает ресурсы из TypedArray по индексу.
 *
 * @param context Контекст приложения/активности
 * @param index Индекс элемента
 */
fun TypedArray.getAnimator(context: Context, index: Int): Animator? {
    val resourceId = getResourceId(index, 0)

    if (resourceId != 0) {
        return AnimatorInflater.loadAnimator(context, resourceId)
    }

    return null
}