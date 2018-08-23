package com.sudox.android.common.repository.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManager.KEY_ACCOUNT_NAME
import android.os.AsyncTask
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sudox.android.common.auth.KEY_ACCOUNT_ID
import com.sudox.android.common.auth.SudoxAccount
import com.sudox.android.common.enums.State
import com.sudox.android.database.SudoxDatabase



class AccountRepository(private val accountManager: AccountManager,
                        private val sudoxDatabase: SudoxDatabase) {

    // Account type
    private val accountType = "com.sudox.account"

    fun saveAccount(account: SudoxAccount): LiveData<State> {
        val mutableLiveData = MutableLiveData<State>()
        val accountInstance = Account(account.name, accountType)

        // Remove accounts
        removeAccounts()

        // Add account
        accountManager.addAccountExplicitly(accountInstance, null, null)

        // Write account data
        accountManager.setUserData(accountInstance, KEY_ACCOUNT_ID, account.id)
        accountManager.setUserData(accountInstance, KEY_ACCOUNT_NAME, account.name)
        accountManager.setPassword(accountInstance, account.token)

        // Notify, that operation was completed
        mutableLiveData.postValue(State.SUCCESS)
        return mutableLiveData
    }

    @Suppress("DEPRECATION")
    fun removeAccounts() {
        val accounts = accountManager.accounts

        for (account in accounts) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                accountManager.removeAccountExplicitly(account)
            } else {
                accountManager.removeAccount(account, null, null)
            }
        }
    }

    fun deleteData(){
        AsyncTask.execute {
            sudoxDatabase.clearAllTables()
        }
    }

    fun getAccount(): LiveData<SudoxAccount?> {
        val mutableLiveData = MutableLiveData<SudoxAccount?>()
        val accounts = accountManager.accounts

        if (accounts.isNotEmpty()) {
            val account = accounts[accounts.size - 1]
            val accountId = accountManager.getUserData(account, KEY_ACCOUNT_ID)
            val accountName = accountManager.getUserData(account, KEY_ACCOUNT_NAME)
            val accountToken = accountManager.getPassword(account)

            // Account instance
            val sudoxAccount = SudoxAccount(accountId, accountName, accountToken)

            // Send account to the single
            mutableLiveData.postValue(sudoxAccount)
        } else {
            mutableLiveData.postValue(null)
        }
        return mutableLiveData
    }
}