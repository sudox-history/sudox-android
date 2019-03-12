package com.sudox.android.data.repositories.users

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Build
import com.sudox.android.data.database.SudoxDatabase
import com.sudox.android.data.database.model.user.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(private val database: SudoxDatabase,
                                            private val context: Context) {

    private val accountManager by lazy { AccountManager.get(context) }

    companion object {
        val ACCOUNT_TYPE = "com.sudox.android"
        val KEY_ACCOUNT_ID = "authAccountId"
    }

    fun saveAccount(token: String, user: User) {
        removeAccounts()

        // Create new account
        val account = Account(user.name, ACCOUNT_TYPE)

        // Save account to storage
        accountManager.addAccountExplicitly(account, null, null)
        accountManager.setUserData(account, KEY_ACCOUNT_ID, user.uid.toString())
        accountManager.setUserData(account, AccountManager.KEY_ACCOUNT_NAME, user.nickname)
        accountManager.setUserData(account, AccountManager.KEY_AUTHTOKEN, token)
    }

    @Suppress("DEPRECATION")
    fun removeAccounts() {
        database.clearAllTables()

        // Remove all account with current accountType ...
        accountManager
                .getAccountsByType(ACCOUNT_TYPE)
                .forEach {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        accountManager.removeAccountExplicitly(it!!)
                    } else {
                        accountManager.removeAccount(it!!, null, null)
                    }
                }
    }

    fun getAccount(): Account? {
        return accountManager
                .getAccountsByType(ACCOUNT_TYPE)
                .lastOrNull()
    }

    fun updateAccount(account: Account, user: User) {
        accountManager.setUserData(account, KEY_ACCOUNT_ID, user.uid.toString())
        accountManager.setUserData(account, AccountManager.KEY_ACCOUNT_NAME, user.nickname)
    }

    fun getAccountId(account: Account): Long? = accountManager.getUserData(account, KEY_ACCOUNT_ID).toLongOrNull()
    fun getAccountToken(account: Account): String? = accountManager.getUserData(account, AccountManager.KEY_AUTHTOKEN)
}