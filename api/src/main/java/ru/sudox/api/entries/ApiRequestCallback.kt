package ru.sudox.api.entries

import io.reactivex.rxjava3.core.SingleEmitter

data class ApiRequestCallback<T : Any>(
        val subjectEmitter: SingleEmitter<T>,
        val dataClass: Class<T>
)