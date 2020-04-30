package ru.sudox.android.account.repository

import android.accounts.Account
import android.accounts.AccountManager
import android.os.Bundle

const val ACCOUNTMANAGER_ACCOUNT_ID_KEY = "account_id"

class AccountRepository(
        private val accountManager: AccountManager,
        private val accountType: String
) {

    /**
     * Добавляет аккаунт в AccountManager.
     * NB! Данные будут зашифрованы в связи с брешью в безопасности на версиях Android 6 и ниже
     *
     * @param id ID аккаунта
     * @param name Имя аккаунта
     * @param secret Токен аккаунта
     */
    fun addAccount(id: String, name: String, secret: String) {
        accountManager.addAccountExplicitly(Account(name, accountType), secret, Bundle().apply {
            putString(AccountManager.KEY_ACCOUNT_NAME, name)
            putString(ACCOUNTMANAGER_ACCOUNT_ID_KEY, id)
        })
    }
}