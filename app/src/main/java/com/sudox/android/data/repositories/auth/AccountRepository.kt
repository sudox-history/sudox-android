package com.sudox.android.data.repositories.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManager.KEY_ACCOUNT_NAME
import android.os.Build
import com.sudox.android.common.helpers.formatPhoneByMask
import com.sudox.android.data.auth.KEY_ACCOUNT_ID
import com.sudox.android.data.auth.SudoxAccount
import com.sudox.android.data.database.SudoxDatabase
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(private val accountManager: AccountManager,
                                            private val sudoxDatabase: SudoxDatabase) {

    // Account direction
    private val accountType = "com.sudox"

    // Cached account
    var cachedAccount: SudoxAccount? = null

    fun saveAccount(account: SudoxAccount) = GlobalScope.async(Dispatchers.IO) {
        val accountInstance = Account(account.phoneNumber, accountType)

        // Remove accounts
        removeAccounts().await()

        // Clear database before old account
        sudoxDatabase.clearAllTables()

        // Add account
        accountManager.addAccountExplicitly(accountInstance, null, null)

        // Write account data
        accountManager.setUserData(accountInstance, KEY_ACCOUNT_ID, account.id.toString())
        accountManager.setUserData(accountInstance, KEY_ACCOUNT_NAME, formatPhoneByMask(account.phoneNumber))
        accountManager.setPassword(accountInstance, account.secret)

        // Update cache
        cachedAccount = account
    }

    fun removeAccounts() = GlobalScope.async(Dispatchers.IO) {
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

            // Update cache
            cachedAccount = null
        }
    }

    fun getAccount() = GlobalScope.async(Dispatchers.IO) {
        val accounts = accountManager.getAccountsByType(accountType)

        if (accounts.isNotEmpty()) {
            val account = accounts[accounts.size - 1]
            val accountId = accountManager.getUserData(account, KEY_ACCOUNT_ID).toLong()
            val accountName = accountManager.getUserData(account, KEY_ACCOUNT_NAME)
            val accountSecret = accountManager.getPassword(account)
            val sudoxAccount = SudoxAccount(accountId, accountName, accountSecret)

            // Update cache
            cachedAccount = sudoxAccount

            // Send account to the single
            return@async sudoxAccount
        } else {
            // Update cache
            cachedAccount = null
            return@async null
        }
    }
}