package com.sudox.sockets;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;

public class SocketClient {

    // Указатель на обьект клиента в нативной памяти.
    private final long pointer;

    /**
     * Конструктор обьекта клиента.
     *
     * @param host - адрес сервера
     * @param port - порт сервера
     */
    public SocketClient(@NonNull String host, short port) {
        System.loadLibrary("ssockets");

        // Creating the client instance in native memory
        this.pointer = createNativeInstance(host, port);
    }

    public interface ClientCallback {
        void socketConnected();
        void socketClosed(boolean error);
        void socketReceive();
    }

    /**
     * Устанавливает соединение с сервером.
     * Возвращает результат в кэллбэк.
     */
    public void connect() {
        connect(pointer);
    }

    /**
     * Закрывает соединение с сервером.
     * Возвращает результат в кэллбэк.
     *
     * @param error - закрыть соединение как из-за ошибки.
     */
    public void close(boolean error) {
        if (opened()) {
            close(pointer, error);
        }
    }

    /**
     * Возвращает статус соединения с сервером.
     */
    public boolean opened() {
        return opened(pointer);
    }

    /**
     * Отправляет данные из буффера на сервер.
     * Отправляет в порядке очереди.
     *
     * @param buffer - буффер для отправки.
     */
    public void sendBuffer(@NonNull ByteBuffer buffer) {
        if (buffer.isDirect()) {
            sendBuffer(pointer, buffer, buffer.limit());
        } else {
            send(pointer, buffer.array());
        }
    }

    /**
     * Отправляет данные на сервер.
     * Отправляет в порядке очереди.
     *
     * @param data - данные для отправки.
     */
    public void send(@NonNull byte[] data) {
        send(pointer, data);
    }

    /**
     * Читает данные с потока.
     * Выдает пустой массив в случае ошибки.
     *
     * @param count - кол-во байтов, которое нужно прочитать.
     */
    @NonNull
    public byte[] readBytes(int count) {
        return readBytes(pointer, count);
    }

    /**
     * Читает данные с потока в буффер.
     * Выдает -1 в случае ошибки.
     *
     * @param buffer - буффер который нужно заполнить.
     * @param count - кол-во байтов для чтения
     */
    public int readToByteBuffer(@NonNull ByteBuffer buffer, int count, int offset) {
        if (!buffer.isDirect()) {
            throw new UnsupportedOperationException("It's method support only direct ByteBuffers!");
        }

        int read = readToBuffer(pointer, buffer, count, offset);

        if (read > 0) {
            buffer.position(buffer.position() + read);
        }

        return read;
    }

    /**
     * Выдает кол-во байтов, доступных для чтения.
     * Возвращает -1 в случае ошибки.
     */
    public int availableBytes() {
        return availableBytes(pointer);
    }

    /**
     * Уставливает обьект для обратного вызова.
     * Внимание! Частый вызов этой функции чреват просадками производительности!
     *
     * @param callback - обьект обратного вызова.
     */
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
