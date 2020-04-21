package ru.sudox.cryptography;

import androidx.annotation.NonNull;

import ru.sudox.cryptography.helpers.NativeHelper;

public class BLAKE2b {

    static {
        NativeHelper.loadMethods();
    }

    /**
     * Рассчитывает хеш данных с помощью алгоритма BLAKE2b-256
     *
     * @param data Данные, хэш которых нужно посчитать
     * @return Массив байтов хеша.
     */
    @NonNull
    public static native byte[] hash(@NonNull byte[] data);
}
