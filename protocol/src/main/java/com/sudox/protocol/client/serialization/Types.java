package com.sudox.protocol.client.serialization;

public class Types {
    public static final byte BOOLEAN = 10;
    public static final byte NUMBER = 20;
    public static final byte STRING = 30;
    public static final byte BUFFER = 40;
    public static final byte ARRAY = 50;
    public static final byte OBJECT = 60;

    public static final int NUMBER_HEADERS_LENGTH = 1;
    public static final int STRING_HEADERS_LENGTH = 2;
    public static final int BUFFER_HEADERS_LENGTH = 2;
    public static final int ARRAY_HEADERS_LENGTH = 1;
    public static final int OBJECT_HEADERS_LENGTH = 1;
    public static final int PARAM_HEADERS_LENGTH = 1;
}