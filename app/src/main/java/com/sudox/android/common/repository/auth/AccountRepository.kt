package com.sudox.android.common.repository.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManager.KEY_ACCOUNT_NAME
import android.os.Build
import com.sudox.android.common.auth.KEY_ACCOUNT_ID
import com.sudox.android.common.auth.SudoxAccount
import com.sudox.android.database.ContactsDao
import io.reactivex.Completable
import io.reactivex.Single

class AccountRepository(private val accountManager: AccountManager,
                        private val contactsDao: ContactsDao) {

    // Account type
    private val accountType = "com.sudox.account"

    fun saveAccount(account: SudoxAccount): Completable = Completable.create {
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
        it.onComplete()
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
        contactsDao.deleteAllContacts()
    }

    fun getAccount(): Single<SudoxAccount?> = Single.unsafeCreate {
        val accounts = accountManager.accounts

        if (accounts.isNotEmpty()) {
            val account = accounts[accounts.size - 1]
            val accountId = accountManager.getUserData(account, KEY_ACCOUNT_ID)
            val accountName = accountManager.getUserData(account, KEY_ACCOUNT_NAME)
            val accountToken = accountManager.getPassword(account)

            // Account instance
            val sudoxAccount = SudoxAccount(accountId, accountName, accountToken)

            // Send account to the single
            it.onSuccess(sudoxAccount)
        } else {
            it.onSuccess(null)
        }
    }
}