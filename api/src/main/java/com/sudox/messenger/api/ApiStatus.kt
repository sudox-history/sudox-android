package com.sudox.messenger.api

import androidx.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@IntDef(ApiStatus.CONNECTION_CLOSED,
        ApiStatus.CONNECTION_INSTALLED,
        ApiStatus.VERSIONS_MISMATCHES)
annotation class ApiStatus {
    companion object {
        const val CONNECTION_CLOSED = 0
        const val CONNECTION_INSTALLED = 1
        const val VERSIONS_MISMATCHES = 2
    }
}