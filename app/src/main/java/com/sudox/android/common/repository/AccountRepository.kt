package com.sudox.android.common.repository

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManager.KEY_ACCOUNT_NAME
import android.accounts.AccountManager.KEY_AUTHTOKEN
import android.os.Build
import android.os.Bundle
import com.sudox.android.common.auth.KEY_ACCOUNT_ID
import com.sudox.android.common.auth.SudoxAccount
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class AccountRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                            private val accountManager: AccountManager) {

    // Account type
    private val accountType = "com.sudox.account"

    fun saveAccount(account: SudoxAccount) {
        val accountInstance = Account(account.name, accountType)

        // User data
        val userDataBundle = Bundle()

        // Save data to the bundle
        with(userDataBundle) {
            putLong(KEY_ACCOUNT_ID, account.id)
            putString(KEY_ACCOUNT_NAME, account.name)
            putString(KEY_AUTHTOKEN, account.token)
        }

        // Remove all accounts
        removeAccounts()

        // Add account
        accountManager.addAccountExplicitly(accountInstance, null, userDataBundle)
    }

    fun removeAccounts() {
        accountManager
                .accounts
                .forEach {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        accountManager.removeAccount(it, null, null, null)
                    } else {
                        accountManager.removeAccount(it, null, null)
                    }
                }
    }

    fun getAccount(): SudoxAccount? {
        val accounts = accountManager.accounts

        // Check, that accounts existing
        return if (accounts.isNotEmpty()) {
            val account = accounts[0]

            SudoxAccount(
                    id = accountManager.getUserData(account, KEY_ACCOUNT_ID).toLong(),
                    name = accountManager.getUserData(account, KEY_ACCOUNT_NAME),
                    token = accountManager.getUserData(account, KEY_AUTHTOKEN))
        } else { null }
    }
}