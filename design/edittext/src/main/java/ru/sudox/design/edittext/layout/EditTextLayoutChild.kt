package ru.sudox.design.edittext.layout

interface EditTextLayoutChild {

    /**
     * Задает цвет обводке.
     *
     * @param layout EditTextLayout, который запросил смену цвета
     * @param width Толщина, которую нужно присвоить обводке
     * @param color Цвет, который нужно присвоить обводке
     */
    fun changeStrokeColor(layout: EditTextLayout, width: Int, color: Int)

    /**
     * Включает/выключает поле ввода.
     * Блокируется ввод, EditText принимает соответствующий вид.
     *
     * @param enabled Включить или выключить EditText?
     */
    fun setEnabled(enabled: Boolean)

    /**
     * Можно ли игнорировать левый отступ ошибки?
     *
     * @return True - если можно, False - если нельзя
     */
    fun canIgnoreErrorLeftMargin(): Boolean
}