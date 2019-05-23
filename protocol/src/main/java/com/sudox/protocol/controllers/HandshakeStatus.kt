package com.sudox.protocol.controllers

import android.support.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@IntDef(HandshakeStatus.NOT_STARTED,
        HandshakeStatus.WAIT_SERVER_PUBLIC_KEY,
        HandshakeStatus.SUCCESS)
annotation class HandshakeStatus {
    companion object {
        const val NOT_STARTED = 0
        const val WAIT_SERVER_PUBLIC_KEY = 1
        const val SUCCESS = 2
    }
}