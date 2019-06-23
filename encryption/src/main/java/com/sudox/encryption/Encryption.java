package com.sudox.encryption;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class Encryption {

    static {
        System.loadLibrary("sencryption");
    }

    @NotNull
    public static native byte[] generateBytes(int count);

    public static native int generateInt(int start, int end);

    @Nullable
    public static native byte[] encryptWithAES(@NonNull byte[] key, @NonNull byte[] iv, @NonNull byte[] message);

    @Nullable
    public static native byte[] decryptWithAES(@NotNull byte[] key, @NotNull byte[] iv, @NotNull byte[] message);

    @NonNull
    public static native byte[] computeHMAC(@NonNull byte[] key, @NonNull byte[] message);

    public static native boolean verifyHMAC(@NotNull byte[] key, @NotNull byte[] message, @NonNull byte[] hmac);

    public static native boolean verifySignature(@NotNull byte[] message, @NotNull byte[] signature);

    @NonNull
    public static native ECDHSession startECDH();

    @Nullable
    public static native byte[] finishECDH(long keyPairPointer, @NonNull byte[] publicKey);

    public static native void closeECDH(long keyPairPointer);
}
