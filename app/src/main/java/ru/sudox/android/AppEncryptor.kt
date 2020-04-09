package ru.sudox.android

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import ru.sudox.android.core.inject.APP_CONTEXT_NAME
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

private const val DATABASE_KEY_ALIAS = "sudox_database_key_alias"
private const val DATABASE_KEY_IV_PREF = "database_key_iv_pref"
private const val DATABASE_KEY_PREF = "database_key_pref"

@Singleton
class AppEncryptor @Inject constructor(
        @Named(APP_CONTEXT_NAME) val context: Context
) {

    private val sharedPreferences = context.getSharedPreferences("SUDOX_ENCRYPTOR_PREFS", Context.MODE_PRIVATE)
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    /**
     * Получает или генерирует ключ для шифрования базы данных.
     * Сгенерированный ключ БД шифруется с помощью ключа, сохраненного в AndroidKeyStore.
     *
     * @return Ключ шифрования БД
     */
    fun getDatabaseKey(): ByteArray {
        val keyEntry = keyStore.getEntry(DATABASE_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry

        if (keyEntry != null) {
            val encodedSecretKey = sharedPreferences.getString(DATABASE_KEY_PREF, null)

            if (encodedSecretKey != null) {
                val decodedIv = Base64.decode(sharedPreferences.getString(DATABASE_KEY_IV_PREF, null), Base64.DEFAULT)

                return with(Cipher.getInstance("AES/CTR/NoPadding")) {
                    init(Cipher.DECRYPT_MODE, keyEntry.secretKey, IvParameterSpec(decodedIv))
                    doFinal(Base64.decode(encodedSecretKey, Base64.DEFAULT))
                }
            }
        }

        if (keyEntry != null) {
            keyStore.deleteEntry(DATABASE_KEY_ALIAS)
        }

        val databaseSecretKey = with(KeyGenerator.getInstance("AES")) {
            init(512)
            generateKey()
        }.encoded

        val secretKey = with(KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")) {
            init(KeyGenParameterSpec.Builder(DATABASE_KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CTR)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build())

            generateKey()
        }

        val databaseKeyCipher = Cipher.getInstance("AES/CTR/NoPadding")
        val encryptedDatabaseKey = with(databaseKeyCipher) {
            init(Cipher.ENCRYPT_MODE, secretKey)
            doFinal(databaseSecretKey)
        }

        sharedPreferences
                .edit()
                .putString(DATABASE_KEY_PREF, Base64.encodeToString(encryptedDatabaseKey, Base64.DEFAULT))
                .putString(DATABASE_KEY_IV_PREF, Base64.encodeToString(databaseKeyCipher.iv, Base64.DEFAULT))
                .apply()

        return databaseSecretKey
    }
}