package ru.sudox.android.core.exceptions

/**
 * Исключение для проверок отправляемых данных.
 *
 * @param fields Индексы неправильных параметров, из-за
 * которых функция выбросила исключение.
 */
class InvalidFieldFormatException(
        val fields: HashSet<Int>
) : Exception()