package com.sudox.android.common.repository

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManager.KEY_ACCOUNT_NAME
import android.accounts.AccountManager.KEY_AUTHTOKEN
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

        accountManager.addAccountExplicitly(accountInstance, null, userDataBundle)
    }

    fun getAccount(): MutableLiveData<SudoxAccount> {
        val account = accountManager.accounts[0]

        // Check, that account exists
        return if (account != null) {
            accountLiveData.postValue(
                    SudoxAccount(
                            id = accountManager.getUserData(account, KEY_ACCOUNT_ID).toLong(),
                            name = accountManager.getUserData(account, KEY_ACCOUNT_NAME),
                            token = accountManager.getUserData(account, KEY_AUTHTOKEN)
                    )
            )
        } else {
            accountLiveData.postValue(null)
        }
    }
}