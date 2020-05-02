package ru.sudox.cryptography.entries;

import androidx.annotation.NonNull;

public class SecretKeyPair {

    private byte[] receiveKey;
    private byte[] sendKey;

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public SecretKeyPair(@NonNull byte[] receiveKey, @NonNull byte[] sendKey) {
        this.receiveKey = receiveKey;
        this.sendKey = sendKey;
    }

    @NonNull
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public byte[] getReceiveKey() {
        return receiveKey;
    }

    @NonNull
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public byte[] getSendKey() {
        return sendKey;
    }
}
