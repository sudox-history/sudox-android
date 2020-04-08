package ru.sudox.api

import com.sudox.messenger.api.R

const val OK_ERROR_CODE = 0
const val SERVICE_UNAVAILABLE_ERROR_CODE = 1
const val ACCESS_DENIED_ERROR_CODE = 2
const val REQUEST_FORMAT_INVALID_CODE = 3
const val NO_INTERNET_CONNECTION = Int.MAX_VALUE

val errorNames = hashMapOf(
        SERVICE_UNAVAILABLE_ERROR_CODE to R.string.service_unavailable
)

fun parseInternetErrors() {

}