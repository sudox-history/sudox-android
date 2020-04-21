package ru.sudox.cryptography.helpers;

import android.util.Log;

public class NativeHelper {

    /**
     * Загружает методы библиотеки шифрования в заданном классе.
     */
    public static void loadMethods() {
        try {
            System.loadLibrary("libcryptography");
        } catch (Exception e) {
            Log.e("Sudox Cryptography", "Couldn't load cryptography library", e);
        }
    }
}
