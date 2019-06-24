package com.sudox.protocol.client.network;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;

public class SocketClient {

    private final long pointer;

    public SocketClient(@NonNull String host, short port) {
        System.loadLibrary("ssockets");
        this.pointer = createNativeInstance(host, port);
    }

    public interface ClientCallback {
        void socketConnected();

        void socketClosed(boolean error);

        void socketReceive();
    }

    public void connect() {
        connect(pointer);
    }

    public void close(boolean error) {
        if (opened()) {
            close(pointer, error);
        }
    }

    public boolean opened() {
        return opened(pointer);
    }

    public void sendBuffer(@NonNull ByteBuffer buffer) {
        if (buffer.isDirect()) {
            sendBuffer(pointer, buffer, buffer.limit());
        } else {
            send(pointer, buffer.array());
        }
    }

    public void send(@NonNull byte[] data) {
        send(pointer, data);
    }

    @NonNull
    public byte[] readBytes(int count) {
        return readBytes(pointer, count);
    }

    /**
     * Returns count of read bytes.
     * Returns -1 if error thrown.
     * <p>
     * P.S.: Supports only direct buffers!
     */
    public int readToByteBuffer(@NonNull ByteBuffer buffer, int count, int offset) {
        int read = readToBuffer(pointer, buffer, count, offset);

        if (read > 0) {
            buffer.position(buffer.position() + read);
        }

        return read;
    }

    /**
     * Returns count of available bytes.
     * Returns -1 if error thrown.
     */
    public int availableBytes() {
        return availableBytes(pointer);
    }

    public void callback(@NonNull ClientCallback callback) {
        callback(pointer, callback);
    }

    private native long createNativeInstance(String host, short port);

    private native void connect(long pointer);

    private native void close(long pointer, boolean error);

    private native boolean opened(long pointer);

    private native void send(long pointer, byte[] data);

    private native void sendBuffer(long pointer, ByteBuffer buffer, int length);

    private native byte[] readBytes(long pointer, int count);

    private native int readToBuffer(long pointer, ByteBuffer buffer, int count, int offset);

    private native int availableBytes(long pointer);

    private native void callback(long pointer, ClientCallback callback);
}
