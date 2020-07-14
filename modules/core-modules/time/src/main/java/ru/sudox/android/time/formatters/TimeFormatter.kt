package ru.sudox.android.time.formatters

import android.content.Context
import java.time.LocalDateTime

interface TimeFormatter {

    /**
     * Вызывается если событие произошло в другом году.
     *
     * @param context Контекст приложения/активности
     * @param yearsAgo Сколько прошло лет
     * @param request Время, которое было предложено для форматирования
     * @param relative Относительное время, которое было предложено для форматирования
     * @param useTwelveHoursFormat Использовать ли 12-и часовой формат времени?
     */
    fun onEventOccurredInAnotherYear(
        context: Context,
        yearsAgo: Int,
        request: LocalDateTime,
        relative: LocalDateTime,
        useTwelveHoursFormat: Boolean
    ): String

    /**
     * Вызывается если событие произошло в текущем году.
     *
     * @param context Контекст приложения/активности
     * @param request Время, которое было предложено для форматирования
     * @param relative Относительное время, которое было предложено для форматирования
     * @param useTwelveHoursFormat Использовать ли 12-и часовой формат времени?
     */
    fun onEventOccurredInSameYear(
        context: Context,
        request: LocalDateTime,
        relative: LocalDateTime,
        useTwelveHoursFormat: Boolean
    ): String

    /**
     * Вызывается если событие произошло в течении недели.
     *
     * @param context Контекст приложения/активности
     * @param daysAgo Сколько прошло дней.
     * @param request Время, которое было предложено для форматирования
     * @param relative Относительное время, которое было предложено для форматирования
     * @param useTwelveHoursFormat Использовать ли 12-и часовой формат времени?
     */
    fun onEventOccurredBetween2And7DaysAgo(
        context: Context,
        daysAgo: Int,
        request: LocalDateTime,
        relative: LocalDateTime,
        useTwelveHoursFormat: Boolean
    ): String

    /**
     * Вызывается если событие произошло вчера.
     *
     * @param context Контекст приложения/активности
     * @param request Время, которое было предложено для форматирования
     * @param relative Относительное время, которое было предложено для форматирования
     * @param useTwelveHoursFormat Использовать ли 12-и часовой формат времени?
     */
    fun onEventOccurredYesterday(
        context: Context,
        request: LocalDateTime,
        relative: LocalDateTime,
        useTwelveHoursFormat: Boolean
    ): String

    /**
     * Вызывается если событие произошло 12-24 часа назад.
     *
     * @param context Контекст приложения/активности
     * @param hoursAgo Сколько прошло часов
     * @param request Время, которое было предложено для форматирования
     * @param relative Относительное время, которое было предложено для форматирования
     * @param useTwelveHoursFormat Использовать ли 12-и часовой формат времени?
     */
    fun onEventOccurredBetween12And24HoursAgo(
        context: Context,
        hoursAgo: Int,
        request: LocalDateTime,
        relative: LocalDateTime,
        useTwelveHoursFormat: Boolean
    ): String

    /**
     * Вызывается если событие произошло 1-12 часов назад.
     *
     * @param context Контекст приложения/активности
     * @param hoursAgo Сколько прошло часов
     * @param request Время, которое было предложено для форматирования
     * @param relative Относительное время, которое было предложено для форматирования
     * @param useTwelveHoursFormat Использовать ли 12-и часовой формат времени?
     */
    fun onEventOccurredBetween1And12HoursAgo(
        context: Context,
        hoursAgo: Int,
        request: LocalDateTime,
        relative: LocalDateTime,
        useTwelveHoursFormat: Boolean
    ): String

    /**
     * Вызывается если событие произошло 1-60 минут назад.
     *
     * @param context Контекст приложения/активности
     * @param minutesAgo Сколько прошло часов
     * @param request Время, которое было предложено для форматирования
     * @param relative Относительное время, которое было предложено для форматирования
     * @param useTwelveHoursFormat Использовать ли 12-и часовой формат времени?
     */
    fun onEventOccurredBetween1And60MinutesAgo(
        context: Context,
        minutesAgo: Int,
        request: LocalDateTime,
        relative: LocalDateTime,
        useTwelveHoursFormat: Boolean
    ): String

    /**
     * Вызывается если событие произошло сейчас (1-60 секунд назад).
     *
     * @param context Контекст приложения/активности
     * @param request Время, которое было предложено для форматирования
     * @param relative Относительное время, которое было предложено для форматирования
     * @param useTwelveHoursFormat Использовать ли 12-и часовой формат времени?
     */
    fun onEventOccurredNow(context: Context, request: LocalDateTime, relative: LocalDateTime, useTwelveHoursFormat: Boolean): String
}