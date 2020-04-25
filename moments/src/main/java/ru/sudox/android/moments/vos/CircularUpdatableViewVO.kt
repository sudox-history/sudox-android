package ru.sudox.android.moments.vos

import android.content.Context
import android.graphics.Canvas
import ru.sudox.android.media.images.views.avatar.AvatarVO

const val NOT_SHOW_CONTENT_ON_STROKE_ANGLE = -1.0

/**
 * ViewObject для CircularUpdatableView.
 */
interface CircularUpdatableViewVO : AvatarVO {

    /**
     * Возвращает текст заголовка
     *
     * @param context Контекст активности/приложения
     * @return Текст заголовка, null если его не нужно отображать
     */
    fun getTitle(context: Context): String?

    /**
     * Возвращает угол расположения контента на окружности
     *
     * @return Значение в диапазоне [0; 360],
     * в противном случае контент не будет отрисовываться.
     */
    fun getContentOnCircleAngle(): Double

    /**
     * Рисует контент на кружке.
     *
     * @param context Контекст активности/приложения
     * @param canvas Canvas для отрисовки
     * @param centerX Центр контента на оси X для высчитанного угла
     * @param centerY Центр контента на оси Y для высчитанного угла
     * @param storage Хранилище данных для рендера
     */
    fun drawContentOnCircle(context: Context, canvas: Canvas, centerX: Float, centerY: Float, storage: Any?) {
    }

    /**
     * Создает хранилище для рендера.
     * Можно использовать любой тип обьектов.
     *
     * @param context Контекст активности/приложения
     * @return Обьект хранилища (null если не нужно)
     */
    fun createVoStorage(context: Context): Any? {
        return null
    }

    /**
     * Определяет подсвеченность кружка.
     * В зависимости от этого определяется толщина и цвет обводки.
     *
     * @return Активна ли обводка?
     */
    fun isActive(): Boolean
}