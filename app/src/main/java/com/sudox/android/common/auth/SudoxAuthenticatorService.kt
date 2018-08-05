package com.sudox.android.common.auth

import android.app.Service
import android.content.Intent
import android.os.IBinder

class SudoxAuthenticatorService : Service() {
    // Authenticator instance
    private val authenticator = SudoxAuthenticator(this)

    // Binder
    override fun onBind(intent: Intent?): IBinder = authenticator.iBinder
}