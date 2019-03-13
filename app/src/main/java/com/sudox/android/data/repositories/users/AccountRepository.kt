package com.sudox.android.data.repositories.users

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Build
import com.sudox.android.data.database.SudoxDatabase
import com.sudox.android.data.database.model.user.User
import java.util.concurrent.Semaphore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(private val database: SudoxDatabase,
                                            private val context: Context) {

    private val accountManager by lazy { AccountManager.get(context) }

    companion object {
        const val ACCOUNT_TYPE = "com.sudox.android"
        const val KEY_ACCOUNT_ID = "authAccountId"
    }

    /**
     * Сохраняет аккаунт в защищенное хранилище.
     * Можно сохранить максимум 1 аккаунт, все предыдущие аккаунты будут удалены!
     *
     * @param token - токен доступа
     * @param user - пользователь, с которого выполнен вход (нужен для отображения некоторых данных)
     */
    fun saveAccount(token: String, user: User) {
        var account = Account(user.name, ACCOUNT_TYPE)

        // Remove old accounts ...
        removeUnusedAccounts(account.name)

        // Try save account to storage
        if (accountManager.addAccountExplicitly(account, null, null)) {
            accountManager.setUserData(account, KEY_ACCOUNT_ID, user.uid.toString())
            accountManager.setUserData(account, AccountManager.KEY_ACCOUNT_NAME, user.nickname)
            accountManager.setUserData(account, AccountManager.KEY_AUTHTOKEN, token)
        } else {
            account = getAccount() ?: return

            // Update data (name not updated because it not changed) ...
            accountManager.setUserData(account, KEY_ACCOUNT_ID, user.uid.toString())
            accountManager.setUserData(account, AccountManager.KEY_AUTHTOKEN, token)
        }

        database.clearAllTables()
    }

    /**
     * Удаляет аккаунт из защищенного хранилища.
     *
     * Для 21-й и менее версий реализована синхронизация метода removeAccounts(...) в целях
     * предотвращения десинхронизации данных.
     *
     * @param account - аккаунт для удаления.
     */
    fun removeAccount(account: Account) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            val semaphore = Semaphore(0)

            @Suppress("DEPRECATION")
            accountManager.removeAccount(account, {
                semaphore.release()
            }, null)

            // Waiting where operation will finished
            semaphore.acquire()
        } else {
            accountManager.removeAccountExplicitly(account)
        }
    }

    /**
     * Чистит БД приложения и удаляет все аккаунты, относящиеся к приложению.
     *
     * Для API 21 реализован механизм синхронизации вызова метода removeAccount, чтобы избежать
     * десинхронизации данных.
     */
    fun removeAccounts() {
        accountManager
                .getAccountsByType(ACCOUNT_TYPE)
                .forEach { removeAccount(it!!) }

        database.clearAllTables()
    }

    /**
     * Удаляет неиспользуемые аккаунты.
     * Неиспользуемыми аккаунтами считаются аккаунты, имя которых не соответствует занятому.
     *
     * @param busyName - занятое имя.
     */
    fun removeUnusedAccounts(busyName: String) {
        accountManager
                .getAccountsByType(ACCOUNT_TYPE)
                .filter { it.name != busyName }
                .forEach { removeAccount(it!!) }
    }

    /**
     * Обновляет данные пользователя аккаунта.
     *
     * @param account - аккаунт, в котором нужно обновить данные.
     * @param user - пользователь аккаунта.
     */
    fun updateAccount(account: Account, user: User) {
        accountManager.setUserData(account, KEY_ACCOUNT_ID, user.uid.toString())
        accountManager.setUserData(account, AccountManager.KEY_ACCOUNT_NAME, user.nickname)
    }

    fun getAccount(): Account? = accountManager.getAccountsByType(ACCOUNT_TYPE).lastOrNull()
    fun getAccountId(account: Account): Long? = accountManager.getUserData(account, KEY_ACCOUNT_ID).toLongOrNull()
    fun getAccountToken(account: Account): String? = accountManager.getUserData(account, AccountManager.KEY_AUTHTOKEN)
}