package ru.sudox.api.exceptions

class ApiException(
        val code: Int
) : Exception()