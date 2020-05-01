package ru.sudox.android.account.repository

import android.accounts.Account
import android.accounts.AccountManager
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import ru.sudox.android.account.BuildConfig
import ru.sudox.android.account.entries.AccountData
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec

private const val ACCOUNTMANAGER_KEY_ALIAS = "sudox_account_manager"
private const val ACCOUNTMANAGER_ACCOUNT_DATA_KEY = "account_data"
private const val ACCOUNTMANAGER_ACCOUNT_IV_KEY = "account_iv"

class AccountRepository(
        private val objectMapper: ObjectMapper,
        private val accountManager: AccountManager,
        private val accountType: String
) {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    /**
     * Добавляет аккаунт в AccountManager.
     * NB! Данные будут зашифрованы в связи с брешью в безопасности на версиях Android 6 и ниже
     *
     * @param data Информация об аккаунте
     */
    fun addAccount(data: AccountData) {
        val key = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(KeyGenParameterSpec
                    .Builder(ACCOUNTMANAGER_KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CTR)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build())
        }.generateKey()

        val cipher = Cipher.getInstance("AES/CTR/NoPadding").apply { init(Cipher.ENCRYPT_MODE, key) }
        val serialized = objectMapper.writeValueAsBytes(data)
        val encrypted = Base64.encodeToString(with(cipher) {
            doFinal(serialized)
        }, Base64.DEFAULT)

        val result = accountManager.addAccountExplicitly(Account(data.nickname, accountType), null, Bundle().apply {
            putString(ACCOUNTMANAGER_ACCOUNT_DATA_KEY, encrypted)
            putString(ACCOUNTMANAGER_ACCOUNT_IV_KEY, Base64.encodeToString(cipher.iv, Base64.DEFAULT))
            putString(AccountManager.KEY_ACCOUNT_NAME, data.nickname)
        })

        if (BuildConfig.DEBUG) {
            if (result) {
                Log.d("Sudox Account", "Account ${data.id} with nickname ${data.nickname} successfully created")
            } else {
                Log.d("Sudox Account", "Account with with nickname ${data.nickname} already exists!")
            }
        }
    }

    /**
     * Получает информацию об аккаунте
     * NB! Также производит её дешифровку в связи с уязвимостью AccountManager на версиях Android 6 и ниже.
     *
     * @return Обьект с информацией об аккаунте (null если аккаунт не был найден)
     */
    fun getAccountData(): AccountData? {
        // Пока не поддерживаем многоаккаунтность
        val account = accountManager.getAccountsByType(accountType).firstOrNull() ?: return null
        val data = Base64.decode(accountManager.getUserData(account, ACCOUNTMANAGER_ACCOUNT_DATA_KEY), Base64.DEFAULT)
        val iv = Base64.decode(accountManager.getUserData(account, ACCOUNTMANAGER_ACCOUNT_IV_KEY), Base64.DEFAULT)

        return try {
            val key = (keyStore.getEntry(ACCOUNTMANAGER_KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey

            objectMapper.readValue(with(Cipher.getInstance("AES/CTR/NoPadding")) {
                init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
                doFinal(data)
            }, AccountData::class.java)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.d("Sudox Account", "Error during account decryption/serialization. It will be removed!", e)
            }

            accountManager.removeAccountExplicitly(account)
            null
        }
    }

    /**
     * Удаляет аккаунт из AccountManager по ID
     * NB! Ключ шифрования тоже будет удален
     */
    fun removeAccount() {
        val account = accountManager.getAccountsByType(accountType).firstOrNull()

        if (account != null) {
            accountManager.removeAccountExplicitly(account)
            Log.d("Sudox Account", "Account removed by user/app.")
        } else if (BuildConfig.DEBUG) {
            Log.d("Sudox Account", "Couldn't remove account because it not found.")
        }
    }

    /**
     * Удаляет ключ шифрования из KeyStore.
     */
    internal fun removeKey() {
        try {
            keyStore.deleteEntry(ACCOUNTMANAGER_KEY_ALIAS)

            if (BuildConfig.DEBUG) {
                Log.d("Sudox Account", "account manager key successfully removed.")
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.d("Sudox Account", "Error during account manager key removing!", e)
            }
        }
    }
}