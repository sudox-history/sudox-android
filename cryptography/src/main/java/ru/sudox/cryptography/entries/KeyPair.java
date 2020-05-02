package ru.sudox.cryptography.entries;

import androidx.annotation.NonNull;

public class KeyPair {

    private byte[] publicKey;
    private byte[] secretKey;

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public KeyPair(@NonNull byte[] publicKey, @NonNull byte[] secretKey) {
        this.publicKey = publicKey;
        this.secretKey = secretKey;
    }

    @NonNull
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public byte[] getPublicKey() {
        return publicKey;
    }

    @NonNull
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public byte[] getSecretKey() {
        return secretKey;
    }
}
