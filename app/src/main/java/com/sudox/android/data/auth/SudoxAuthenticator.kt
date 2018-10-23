package com.sudox.android.data.auth

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import com.sudox.android.ui.splash.SplashActivity

internal const val AUTH_ACCOUNT_MANAGER_START = 0
internal const val AUTH_KEY = "auth"

class SudoxAuthenticator(private val serviceContext: SudoxAuthenticatorService) : AbstractAccountAuthenticator(serviceContext) {

    override fun getAuthTokenLabel(p0: String?) = null
    override fun confirmCredentials(p0: AccountAuthenticatorResponse?, p1: Account?, p2: Bundle?) = null
    override fun updateCredentials(p0: AccountAuthenticatorResponse?, p1: Account?, p2: String?, p3: Bundle?) = null
    override fun getAuthToken(p0: AccountAuthenticatorResponse?, p1: Account?, p2: String?, p3: Bundle?) = null
    override fun hasFeatures(p0: AccountAuthenticatorResponse?, p1: Account?, p2: Array<out String>?) = null
    override fun editProperties(p0: AccountAuthenticatorResponse?, p1: String?) = null
    override fun addAccount(p0: AccountAuthenticatorResponse?, p1: String?, p2: String?, p3: Array<out String>?, p4: Bundle?) = null
}