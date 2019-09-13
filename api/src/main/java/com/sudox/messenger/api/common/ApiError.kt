package com.sudox.messenger.api.common

import androidx.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@IntDef(ApiError.NOT_CONNECTED, ApiError.INVALID_FORMAT)
annotation class ApiError {
    companion object {
        const val NOT_CONNECTED = -1
        const val INVALID_FORMAT = 1
        const val INVALID_PHONE = 2
        const val INVALID_CODE = 3
        const val INVALID_KEY = 4
    }
}