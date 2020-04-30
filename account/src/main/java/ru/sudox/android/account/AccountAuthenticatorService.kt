package ru.sudox.android.account

import android.app.Service
import android.content.Intent
import android.os.IBinder
import ru.sudox.android.account.inject.AccountComponent
import ru.sudox.android.core.CoreLoader
import javax.inject.Inject

class AccountAuthenticatorService : Service() {

    @Inject
    @JvmField
    var authenticator: AccountAuthenticator? = null

    override fun onCreate() {
        ((applicationContext as CoreLoader).getComponent() as AccountComponent).inject(this)
    }

    override fun onBind(intent: Intent?): IBinder {
        return authenticator!!.iBinder
    }
}