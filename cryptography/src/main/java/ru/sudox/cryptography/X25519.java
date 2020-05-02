package ru.sudox.cryptography;

import androidx.annotation.NonNull;

import ru.sudox.cryptography.entries.KeyPair;
import ru.sudox.cryptography.entries.SecretKeyPair;
import ru.sudox.cryptography.helpers.NativeHelper;

public class X25519 {

    public static final int PUBLIC_KEY_LENGTH = 32;
    public static final int SECRET_KEY_LENGTH = 32;

    static {
        NativeHelper.loadMethods();
    }

    /**
     * Генерирует пару ключей X25519.
     * Ключа генерируется размером в 128 бит.
     *
     * @return Обьект с парой ключей.
     */
    @NonNull
    public static native KeyPair generateKeyPair();

    @NonNull
    public static native SecretKeyPair exchange(
            @NonNull byte[] publicKey,
            @NonNull byte[] secretKey,
            @NonNull byte[] recipientPublicKey,
            boolean isServer
    ) throws Exception;
}
