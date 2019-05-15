package com.sudox.cipher;

public class Cipher {

    static {
        System.loadLibrary("scipher");
        initLibrary();
    }

    public static boolean checkEqualsAllBytes(byte[] first, byte[] second) {
        if (first.length != second.length) {
            return false;
        }

        return countNonEqualityBytes(first, second) == 0;
    }

    public static int countNonEqualityBytes(byte[] first, byte[] second) {
        int countNonEqualityBytes = 0;

        for (int i = 0; i < first.length; i++) {
            if (first[i] != second[i]) {
                countNonEqualityBytes++;
            }
        }

        return countNonEqualityBytes;
    }

    public static native byte[] calculateSecretKey(byte[] privateKey, byte[] recipientPublicKey);
    public static native void removeAllKeysPairs();
    public static native void removeKeysPair(int pairId);
    public static native byte[] getPublicKey(int pairId);
    public static native byte[] getPrivateKey(int pairId);
    public static native int generateKeysPair();
    public static native byte[] calculateHMAC(byte[] key, byte[] message);
    public static native byte[] decryptWithAES(byte[] key, byte[] iv, byte[] message);
    public static native byte[] encryptWithAES(byte[] key, byte[] iv, byte[] message);
    public static native boolean verifyMessageWithECDSA(byte[] message, byte[] signature);
    public static native byte[] calculateSHA224(byte[] data);
    public static native byte[] decodeFromBase64(byte[] encoded);
    public static native String encodeToBase64(byte[] decoded);
    public static native String generateBase64(int length);
    public static native byte[] generateBytes(int length);
    private static native void initLibrary();
}
