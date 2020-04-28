package ru.sudox.api.common.exceptions

import java.lang.Exception

/**
 * Исключение подозрения на атаку.
 *
 * Вызывается при непрохождении какой-либо
 * проверки от сервера.
 */
class AttackSuspicionException : Exception()