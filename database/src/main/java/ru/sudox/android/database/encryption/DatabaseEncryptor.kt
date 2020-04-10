package ru.sudox.android.database.encryption

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

private const val DATABASE_KEY_ALIAS = "sudox_database_key_alias"

object DatabaseEncryptor {

    private var secretKey: SecretKey? = null
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    /**
     * Загружает ключ шифрования базы данных.
     * Если нет ключа, то генерирует сам ключ и.
     */
    fun loadKey() {
        secretKey = (keyStore.getEntry(DATABASE_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.secretKey
                ?: with(KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")) {
                    init(KeyGenParameterSpec.Builder(DATABASE_KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CTR)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .setKeySize(128)
                            .build())

                    generateKey()
                }
    }

    /**
     * Дешифровывает данные с помощью секретного ключа
     * IV (вектор инициализации) берет с конца последовательности байтов.
     *
     * @param data Данные для расшифровки
     * @return Расшифрованные данные
     */
    fun decryptData(data: ByteArray): ByteArray {
        val iv = data.copyOfRange(data.size - 16, data.size)
        val cipher = Cipher.getInstance("AES/CTR/NoPadding")

        return with(cipher) {
            init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
            doFinal(data, 0, data.size - 16)
        }
    }

    /**
     * Шифрует данные с помощью секретного ключа.
     * Добавляет в конец зашифрованных данных IV (вектор инициализации)
     *
     * @param data Данные для шифровки
     * @return Зашифрованные данные
     */
    fun encryptData(data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CTR/NoPadding")
        val encrypted = with(cipher) {
            init(Cipher.ENCRYPT_MODE, secretKey)
            doFinal(data)
        }

        return encrypted + cipher.iv
    }
}