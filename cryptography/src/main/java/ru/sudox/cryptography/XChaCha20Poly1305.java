package ru.sudox.cryptography;

import androidx.annotation.NonNull;

import ru.sudox.cryptography.helpers.NativeHelper;

public class XChaCha20Poly1305 {

    public static final int KEY_LENGTH = 32;
    public static final int NONCE_LENGTH = 32;

    static {
        NativeHelper.loadMethods();
    }

    /**
     * Шифрует данные с помощью XChaCha20-Poly1305
     *
     * @param key Ключ шифрования (KEY_LENGTH байта)
     * @param nonce Вектор инициализации (NONCE_LENGTH байта)
     * @param data Данные, которые нужно зашифровать
     * @return Массив с зашифрованными данными.
     */
    @NonNull
    public static native byte[] encryptData(@NonNull byte[] key, @NonNull byte[] nonce, @NonNull byte[] data);

    /**
     * Расшифровывает данные с помощью XChaCha20-Poly1305
     *
     * @param key Ключ шифрования (KEY_LENGTH байта)
     * @param nonce Вектор инициализации (NONCE_LENGTH байта)
     * @param cipherText Зашифрованная информация
     * @return Массив с расшифрованной информацией
     * @throws Exception Если не получилось расшифровать данные
     */
    @NonNull
    public static native byte[] decryptData(@NonNull byte[] key, @NonNull byte[] nonce, @NonNull byte[] cipherText) throws Exception;
}
