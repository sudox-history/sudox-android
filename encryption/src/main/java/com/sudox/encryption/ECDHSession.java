package com.sudox.encryption;

import androidx.annotation.NonNull;

@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
public class ECDHSession {

    private long keyPairPointer;
    private byte[] publicKey;

    /**
     * Don't use or remove it's constructor! It's using from native code.
     */
    public ECDHSession(long keyPairPointer, @NonNull byte[] publicKey) {
        this.keyPairPointer = keyPairPointer;
        this.publicKey = publicKey;
    }

    public long getKeyPairPointer() {
        return keyPairPointer;
    }

    @NonNull
    public byte[] getPublicKey() {
        return publicKey;
    }
}
