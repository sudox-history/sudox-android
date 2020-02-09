package com.sudox.design.common

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.content.res.TypedArray
import android.view.ContextThemeWrapper
import android.view.View

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

/**
 * Создает стилизованную по стилям View
 *
 * @param context Контекст приложения/активности
 * @param index Индекс элемента
 */
inline fun <reified V : View> TypedArray.createStyledView(context: Context, index: Int): V? {
    val resourceId = getResourceId(index, 0)

    if (resourceId != 0) {
        val themedContext = ContextThemeWrapper(context, resourceId)

        return V::class.java
                .getDeclaredConstructor(Context::class.java)
                .newInstance(themedContext)
    }

    return null
}