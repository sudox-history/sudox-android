package com.sudox.messenger.api

import androidx.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@IntDef(ApiError.NOT_CONNECTED)
annotation class ApiError {
    companion object {
        const val NOT_CONNECTED = -1
        const val CODE_NOT_INVALID = 1
    }
}