package ru.sudox.design.circularupdatableview.vos

import android.content.Context
import android.graphics.Canvas
import android.view.View

const val NOT_SHOW_CONTENT_ON_STROKE_ANGLE = -1.0

/**
 * ViewObject для CircularUpdatableView.
 */
interface CircularUpdatableViewVO {

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
     */
    fun drawContentOnCircle(context: Context, canvas: Canvas, centerX: Float, centerY: Float)

    /**
     * Забивает данные в предоставленный View
     *
     * @param view View в который нужно загрузить данные.
     */
    fun bindViewInCenter(view: View)

    /**
     * Удаляет данные из старого View
     *
     * @param view View с которого нужно отгрузить данные
     */
    fun unbindViewInCenter(view: View)

    /**
     * Проверяет тип View в центре.
     * Нужна для переиспользования View в целях оптимизации.
     *
     * @param view Текущая View по центру привязанного CircularUpdatableView
     * @return Совпадают ли типы View?
     */
    fun isViewInCenterTypeSame(view: View): Boolean

    /**
     * Возвращает View в центре View
     * Вызывается только если тип View сменился и требуется новая
     *
     * @param context Контекст активности/приложения
     */
    fun getViewInCenter(context: Context): View

    /**
     * Определяет подсвеченность кружка.
     * В зависимости от этого определяется толщина и цвет обводки.
     *
     * @return Активна ли обводка?
     */
    fun isActive(): Boolean
}