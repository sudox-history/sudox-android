package com.sudox.android.common.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.sudox.android.common.SudoxAuthenticator
import javax.inject.Inject

@SuppressLint("Registered")
class SudoxAuthenticatorService : Service() {
    override fun onBind(p0: Intent?): IBinder {
        val sudoxAuthenticator = SudoxAuthenticator(this)
        return sudoxAuthenticator.iBinder
    }
}