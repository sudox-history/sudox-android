package com.sudox.android.common.auth

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import com.sudox.android.ui.splash.SplashActivity

internal const val AUTH_CODE = 0
internal const val AUTH_KEY = "auth"

class SudoxAuthenticator(private val serviceContext: SudoxAuthenticatorService) : AbstractAccountAuthenticator(serviceContext) {

    override fun getAuthTokenLabel(p0: String?) = null
    override fun confirmCredentials(p0: AccountAuthenticatorResponse?, p1: Account?, p2: Bundle?) = null
    override fun updateCredentials(p0: AccountAuthenticatorResponse?, p1: Account?, p2: String?, p3: Bundle?) = null
    override fun getAuthToken(p0: AccountAuthenticatorResponse?, p1: Account?, p2: String?, p3: Bundle?) = null
    override fun hasFeatures(p0: AccountAuthenticatorResponse?, p1: Account?, p2: Array<out String>?) = null
    override fun editProperties(p0: AccountAuthenticatorResponse?, p1: String?) = null
    override fun addAccount(p0: AccountAuthenticatorResponse?, p1: String?, p2: String?, p3: Array<out String>?, p4: Bundle?): Bundle? {
        val intent = Intent(serviceContext, SplashActivity::class.java)
        intent.putExtra(AUTH_KEY, AUTH_CODE)
        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }
}