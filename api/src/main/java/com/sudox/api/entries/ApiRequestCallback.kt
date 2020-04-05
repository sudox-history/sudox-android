package com.sudox.api.entries

import io.reactivex.rxjava3.subjects.SingleSubject

data class ApiRequestCallback<T : Any>(
        val subject: SingleSubject<T>,
        val dataClass: Class<T>
)