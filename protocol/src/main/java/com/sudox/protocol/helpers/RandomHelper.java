package com.sudox.protocol.helpers;

public class RandomHelper {

    static {
        System.loadLibrary("scipher");
    }

    public static native void initRandom();
}
