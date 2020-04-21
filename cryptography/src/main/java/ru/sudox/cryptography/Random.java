package ru.sudox.cryptography;

import androidx.annotation.NonNull;

import ru.sudox.cryptography.helpers.NativeHelper;

public class Random {

    static {
        NativeHelper.loadMethods();
    }

    /**
     * Генерирует случайную последовательность байтов с помощью криптобезопасных алгоритмов.
     *
     * @param length Длина последовательности байтов
     * @return Массив с случайной последовательностью байтов.
     */
    @NonNull
    public static native byte[] generate(int length);
}