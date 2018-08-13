package com.sudox.android.common.repository.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManager.KEY_ACCOUNT_NAME
import android.os.Build
import com.sudox.android.common.auth.KEY_ACCOUNT_ID
import com.sudox.android.common.auth.SudoxAccount
import javax.inject.Inject

class AccountRepository @Inject constructor(private val accountManager: AccountManager) {

    // Account type
    private val accountType = "com.sudox.account"

    fun saveAccount(account: SudoxAccount) {
        val accountInstance = Account(account.name, accountType)

        removeAccounts()

        // Add account
        accountManager.addAccountExplicitly(accountInstance, null, null)

        // Write account data
        accountManager.setUserData(accountInstance, KEY_ACCOUNT_ID, account.id)
        accountManager.setUserData(accountInstance, KEY_ACCOUNT_NAME, account.name)
        accountManager.setPassword(accountInstance, account.token)
    }

    @Suppress("DEPRECATION")
    private fun removeAccounts() {
        val accounts = accountManager.accounts

        for (account in accounts) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                accountManager.removeAccountExplicitly(account)
            } else {
                accountManager.removeAccount(account, null, null)
            }
        }
    }

    fun getAccount(): SudoxAccount? {
        val accounts = accountManager.accounts

        return if (accounts.isNotEmpty()) {
            val account = accounts[accounts.size - 1]
            val accountId = accountManager.getUserData(account, KEY_ACCOUNT_ID)
            val accountName = accountManager.getUserData(account, KEY_ACCOUNT_NAME)
            val accountToken = accountManager.getPassword(account)

            // Account instance
            SudoxAccount(accountId, accountName, accountToken)
        } else {
            null
        }
    }
}