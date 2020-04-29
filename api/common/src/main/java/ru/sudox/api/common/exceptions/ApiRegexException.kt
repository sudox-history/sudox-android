package ru.sudox.api.common.exceptions

/**
 * Исключение для проверок отправляемых данных.
 *
 * @param fields Индексы неправильных параметров, из-за
 * которых функция выбросила исключение.
 */
class ApiRegexException(
      val fields: HashSet<Int>
) : Exception()