package com.sudox.android.data.auth

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder

class AuthenticatorService : Service() {

    private val authenticator by lazy { Authenticator() }

    /**
     * Выдает Binder авторизатора-заглушки.
     */
    override fun onBind(intent: Intent): IBinder? {
        return if (intent.action == AccountManager.ACTION_AUTHENTICATOR_INTENT) {
            authenticator.iBinder
        } else {
            null
        }
    }

    inner class Authenticator : AbstractAccountAuthenticator(this) {
        override fun getAuthTokenLabel(authTokenType: String?) = null
        override fun editProperties(response: AccountAuthenticatorResponse?, accountType: String?) = null
        override fun addAccount(response: AccountAuthenticatorResponse?,
                                accountType: String?,
                                authTokenType: String?,
                                requiredFeatures: Array<out String>?,
                                options: Bundle?) = null

        override fun confirmCredentials(response: AccountAuthenticatorResponse?,
                                        account: Account?,
                                        options: Bundle?) = null

        override fun getAuthToken(response: AccountAuthenticatorResponse?,
                                  account: Account?,
                                  authTokenType: String?,
                                  options: Bundle?) = null

        override fun updateCredentials(response: AccountAuthenticatorResponse?,
                                       account: Account?,
                                       authTokenType: String?,
                                       options: Bundle?) = null

        override fun hasFeatures(response: AccountAuthenticatorResponse?,
                                 account: Account?,
                                 features: Array<out String>?) = null
    }
}