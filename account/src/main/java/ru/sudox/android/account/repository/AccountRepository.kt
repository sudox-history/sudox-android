package ru.sudox.android.account.repository

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec

private const val ACCOUNTMANAGER_ACCOUNT_ID_KEY = "account_id"
private const val ACCOUNTMANAGER_ACCOUNT_SECRET_IV_KEY = "account_secret_iv"

class AccountRepository(
        private val accountManager: AccountManager,
        private val accountType: String
) {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    /**
     * Добавляет аккаунт в AccountManager.
     * NB! Данные будут зашифрованы в связи с брешью в безопасности на версиях Android 6 и ниже
     *
     * @param id ID аккаунта
     * @param name Имя аккаунта
     * @param secret Токен аккаунта
     */
    fun addAccount(id: String, name: String, secret: String) {
        val key = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(KeyGenParameterSpec
                    .Builder(getAccountKeyAlias(id), KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CTR)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build())
        }.generateKey()

        val cipher = Cipher.getInstance("AES/CTR/NoPadding").apply { init(Cipher.ENCRYPT_MODE, key) }
        val encryptedSecret = Base64.encodeToString(with(cipher) {
            doFinal(secret.toByteArray())
        }, Base64.DEFAULT)

        accountManager.addAccountExplicitly(Account(name, accountType), encryptedSecret, Bundle().apply {
            putString(ACCOUNTMANAGER_ACCOUNT_SECRET_IV_KEY, Base64.encodeToString(cipher.iv, Base64.DEFAULT))
            putString(AccountManager.KEY_ACCOUNT_NAME, name)
            putString(ACCOUNTMANAGER_ACCOUNT_ID_KEY, id)
        })
    }

    /**
     * Получает токен аккаунта из AccountManager'а по ID
     *
     * @param id ID аккаунта
     * @return Токен аккаунта (null если не найден)
     */
    fun getAccountToken(id: String): String? {
        val account = accountManager.getAccountsByType(accountType).find {
            accountManager.getUserData(it, ACCOUNTMANAGER_ACCOUNT_ID_KEY) == id
        } ?: return null

        val encryptedSecret = Base64.decode(accountManager.getPassword(account), Base64.DEFAULT)
        val secretIv = Base64.decode(accountManager.getUserData(account, ACCOUNTMANAGER_ACCOUNT_SECRET_IV_KEY), Base64.DEFAULT)

        val key = (keyStore.getEntry(getAccountKeyAlias(id), null) as KeyStore.SecretKeyEntry).secretKey

        return String(with(Cipher.getInstance("AES/CTR/NoPadding")) {
            init(Cipher.DECRYPT_MODE, key, IvParameterSpec(secretIv))
            doFinal(encryptedSecret)
        })
    }

    /**
     * Удаляет ключ шифрования аккаунта из KeyStore.
     *
     * @param account Account, ключ которого нужно удалить из KeyStore
     */
    internal fun removeAccountKey(account: Account) {
        val id = accountManager.getUserData(account, ACCOUNTMANAGER_ACCOUNT_ID_KEY)
        val alias = getAccountKeyAlias(id)

        keyStore.deleteEntry(alias)
    }

    private fun getAccountKeyAlias(id: String) = "sudox_account_${id}_key"
}