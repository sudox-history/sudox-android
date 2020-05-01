package ru.sudox.android.account

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import ru.sudox.android.account.repository.AccountRepository

class AccountAuthenticator(
        private val context: Context,
        private val activityClass: Class<*>,
        private val accountRepository: AccountRepository
) : AbstractAccountAuthenticator(context) {

    override fun getAccountRemovalAllowed(response: AccountAuthenticatorResponse?, account: Account?): Bundle {
        accountRepository.removeKey()

        return Bundle().apply {
            putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true)
        }
    }

    override fun addAccount(
            response: AccountAuthenticatorResponse?,
            accountType: String?,
            authTokenType: String?,
            requiredFeatures: Array<out String>?,
            options: Bundle?
    ) = Bundle().apply {
        putParcelable(AccountManager.KEY_INTENT, Intent(context, activityClass))
    }

    override fun getAuthTokenLabel(authTokenType: String?): String? {
        return null
    }

    override fun confirmCredentials(response: AccountAuthenticatorResponse?, account: Account?, options: Bundle?): Bundle? {
        return null
    }

    override fun updateCredentials(response: AccountAuthenticatorResponse?, account: Account?, authTokenType: String?, options: Bundle?): Bundle? {
        return null
    }

    override fun getAuthToken(response: AccountAuthenticatorResponse?, account: Account?, authTokenType: String?, options: Bundle?): Bundle? {
        return null
    }

    override fun hasFeatures(response: AccountAuthenticatorResponse?, account: Account?, features: Array<out String>?): Bundle? {
        return null
    }

    override fun editProperties(response: AccountAuthenticatorResponse?, accountType: String?): Bundle? {
        return null
    }
}