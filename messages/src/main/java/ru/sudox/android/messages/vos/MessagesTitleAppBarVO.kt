package ru.sudox.android.messages.vos

import android.content.Context

interface MessagesTitleAppBarVO {

    /**
     * Выдает заголовок в View AppBar'а
     * Например: имя пользователя, название чата
     *
     * @param context Контекст приложения/активности
     * @return Строка с заголовком
     */
    fun getTitle(context: Context): String

    /**
     * Выдает подзаголовок в View AppBar'а
     * Например: время последнего визита, количество участников
     *
     * @param context Контекст приложения/активности
     * @return Строка с заголовком
     */
    fun getSubtitle(context: Context): String
}