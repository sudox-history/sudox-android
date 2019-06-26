package com.sudox.protocol.client.serialization;

public class Types {
    public static final byte BOOLEAN = 1;
    public static final byte NUMBER = 2;
    public static final byte STRING = 3;
    public static final byte BUFFER = 4;
    public static final byte ARRAY = 5;
    public static final byte OBJECT = 6;

    public static final int NUMBER_HEADERS_LENGTH = 1;
    public static final int STRING_HEADERS_LENGTH = 2;
    public static final int BUFFER_HEADERS_LENGTH = 2;
    public static final int ARRAY_HEADERS_LENGTH = 1;
    public static final int OBJECT_HEADERS_LENGTH = 1;
}