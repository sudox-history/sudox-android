package com.sudox.protocol.helpers;

public class CipherHelper {

    static {
        System.loadLibrary("scipher");

        // Init library
        initLibrary();
    }

    /**
     * Производит расчет секретного ключа на основе приватного ключа и публичного ключа собеседника.
     * Вернет пустой массив в случае ошибки.
     *
     * @param privateKey - приватный ключ
     * @param recipientPublicKey - публичный ключ собеседника.
     */
    public static native byte[] calculateSecretKey(byte[] privateKey, byte[] recipientPublicKey);

    /**
     * Удаляет все пары ключей из хранилища.
     */
    public static native void removeAllKeysPairs();

    /**
     * Удаляет пару ключей из хранилища.
     *
     * @param pairId - ID пары, полученный в ходе добавления записи.
     */
    public static native void removeKeysPair(int pairId);

    /**
     * Выдает публичный ключ из хранилища по ID его пары.
     * Если пара не будет найдена, то вернет пустой массив.
     *
     * @param pairId - ID пары, полученный в ходе добавления записи.
     */
    public static native byte[] getPublicKey(int pairId);

    /**
     * Выдает приватный ключ из хранилища по ID его пары.
     * Если пара не будет найдена, то вернет пустой массив.
     *
     * @param pairId - ID пары, полученный в ходе добавления записи.
     */
    public static native byte[] getPrivateKey(int pairId);

    /**
     * Генерирует пару ключей (приватный и публичный) и выдает их ID в хранилище в качестве результата.
     */
    public static native int generateKeysPair();

    /**
     * Вычисляет хэш сообщения с помощью HMAC на основе секретного ключа.
     *
     * @param key     - секретный ключ
     * @param message - сообщение
     */
    public static native byte[] calculateHMAC(byte[] key, byte[] message);

    /**
     * Расшифровывает массив байтов с помощью AES.
     *
     * @param key     - ключ шифрования.
     * @param iv      - инициализирующий вектор.
     * @param message - сообщение, которое нужно расшифровать.
     */
    public static native byte[] decryptWithAES(byte[] key, byte[] iv, byte[] message);

    /**
     * Зашифровывает массив байтов с помощью AES.
     *
     * @param key     - ключ шифрования.
     * @param iv      - инициализирующий вектор.
     * @param message - сообщение, которое нужно зашифровать.
     */
    public static native byte[] encryptWithAES(byte[] key, byte[] iv, byte[] message);

    /**
     * Проверяет данные на соответствие публичного ключа подписи.
     *
     * @param message   - сообщение
     * @param signature - подпись сообщения
     */
    public static native boolean verifyMessageWithECDSA(byte[] message, byte[] signature);

    /**
     * Вычисляет SHA-224 хэш переданного массива байтов.
     *
     * @param data - массив байтов, хэш которого нужны вычислить.
     */
    public static native byte[] calculateSHA224(byte[] data);

    /**
     * Декодирует массив байтов с помощью Base64.
     *
     * @param encoded - массив байтов в формате Base64.
     */
    public static native byte[] decodeFromBase64(byte[] encoded);

    /**
     * Кодирует массив байтов с помощью Base64.
     *
     * @param decoded - массив байтов, который нужно зашифровать.
     */
    public static native String encodeToBase64(byte[] decoded);

    /**
     * Генерирует случайную строку в формате Base64.
     *
     * @param length - кол-во случайных байтов, которые будут закодированы в Base64.
     */
    public static native String generateBase64(int length);

    /**
     * Генерирует массив случайных байтов.
     *
     * @param length - длина массива.
     */
    public static native byte[] generateBytes(int length);

    /**
     * Внутренний метод, инициализирует генератор случайных чисел.
     */
    private static native void initLibrary();
}
