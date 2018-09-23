package com.sudox.android.data.repositories.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManager.KEY_ACCOUNT_NAME
import android.os.Build
import com.sudox.android.data.auth.KEY_ACCOUNT_ID
import com.sudox.android.data.auth.SudoxAccount
import com.sudox.android.data.database.SudoxDatabase
import com.sudox.protocol.ProtocolClient
import kotlinx.coroutines.experimental.async
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                            private val accountManager: AccountManager,
                                            private val sudoxDatabase: SudoxDatabase) {

    // Account type
    private val accountType = "com.sudox"

    fun saveAccount(account: SudoxAccount) = async {
        val accountInstance = Account(account.name, accountType)

        // Remove accounts
        removeAccounts().await()

        // Add account
        accountManager.addAccountExplicitly(accountInstance, null, null)

        // Write account data
        accountManager.setUserData(accountInstance, KEY_ACCOUNT_ID, account.id)
        accountManager.setUserData(accountInstance, KEY_ACCOUNT_NAME, account.name)
        accountManager.setPassword(accountInstance, account.secret)
    }

    @Suppress("DEPRECATION")
    fun removeAccounts() = async {
        val accounts = accountManager.getAccountsByType(accountType)

        if (accounts.isNotEmpty()) {
            sudoxDatabase.clearAllTables()

            for (account in accounts) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    accountManager.removeAccountExplicitly(account)
                } else {
                    accountManager.removeAccount(account, null, null)
                }
            }
        }

        protocolClient.id = null
        protocolClient.secret = null
    }

    fun getAccount() = async {
        val accounts = accountManager.getAccountsByType(accountType)

        if (accounts.isNotEmpty()) {
            val account = accounts[accounts.size - 1]
            val accountId = accountManager.getUserData(account, KEY_ACCOUNT_ID)
            val accountName = accountManager.getUserData(account, KEY_ACCOUNT_NAME)
            val accountSecret = accountManager.getPassword(account)

            // Send account to the single
            return@async SudoxAccount(accountId, accountName, accountSecret)
        } else {
            return@async null
        }
    }

    fun clearSessionData() {
        protocolClient.id = null
        protocolClient.secret = null
    }

    fun setSessionData(account: SudoxAccount) {
        protocolClient.id = account.id
        protocolClient.secret = account.secret
    }
}