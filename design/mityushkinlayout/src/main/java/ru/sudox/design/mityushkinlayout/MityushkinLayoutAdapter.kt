package ru.sudox.design.mityushkinlayout

interface MityushkinLayoutAdapter {

    /**
     * Вызывается при подключении адаптера к Layout'у
     * Можно получить необходимые ресурсы.
     *
     * @param layout Layout, к которому был подключен адаптер
     */
    fun onAttached(layout: MityushkinLayout)

    /**
     * Выдает необходимый шаблон для определенного количества элементов
     *
     * @param count Количество элементов, для которого нужно получить шаблон
     * @return Шаблон для определенного количества элементов
     */
    fun getTemplate(count: Int): MityushkinLayoutTemplate?
}