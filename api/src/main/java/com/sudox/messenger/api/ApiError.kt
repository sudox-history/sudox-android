package com.sudox.messenger.api

import androidx.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@IntDef(ApiError.NOT_CONNECTED)
annotation class ApiError {
    companion object {
        const val NOT_CONNECTED = -1
        const val INVALID_CODE = 1
        const val INVALID_FORMAT = 2
        const val INVALID_KEY = 3
    }
}