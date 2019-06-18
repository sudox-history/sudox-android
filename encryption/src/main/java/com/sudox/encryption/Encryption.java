package com.sudox.encryption;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Encryption {
    static {
        try {
            System.loadLibrary("sencryption");
            initLibrary();
        } catch (UnsatisfiedLinkError ignored) {
        }
    }

    public static boolean checkEqualsAllBytes(@NonNull byte[] first, @NonNull byte[] second) {
        if (first.length != second.length) {
            return false;
        }

        return countNonEqualityBytes(first, second) == 0;
    }

    public static int countNonEqualityBytes(@NonNull byte[] first, @NonNull byte[] second) {
        int countNonEqualityBytes = 0;
        int length = first.length;

        for (int i = 0; i < length; i++) {
            if (first[i] != second[i]) {
                countNonEqualityBytes++;
            }
        }

        return countNonEqualityBytes;
    }

    @Nullable
    public static native byte[] calculateSecretKey(@NonNull byte[] privateKey, @NonNull byte[] recipientPublicKey);
    public static native void removeAllKeysPairs();
    public static native void removeKeysPair(int pairId);

    @Nullable
    public static native byte[] getPublicKey(int pairId);

    @Nullable
    public static native byte[] getPrivateKey(int pairId);
    public static native int generateKeysPair();

    @NonNull
    public static native byte[] calculateHMAC(@NonNull byte[] key, @NonNull byte[] message);

    @Nullable
    public static native byte[] decryptWithAES(@NonNull byte[] key, @NonNull byte[] iv, @NonNull byte[] message);

    @NonNull
    public static native byte[] encryptWithAES(@NonNull byte[] key, @NonNull byte[] iv, @NonNull byte[] message);
    public static native boolean verifyMessageWithECDSA(@NonNull byte[] message, @NonNull byte[] signature);

    @NonNull
    public static native byte[] calculateSHA224(@NonNull byte[] data);

    @NonNull
    public static native byte[] decodeFromBase64(@NonNull byte[] encoded);

    @NonNull
    public static native String encodeToBase64(@NonNull byte[] decoded);

    @NonNull
    public static native String generateBase64(int length);

    @NonNull
    public static native byte[] generateBytes(int length);
    private static native void initLibrary();
}
