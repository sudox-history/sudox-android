#include <jni.h>
#include "SocketClient.h"
#include "callbacks/JNICallback.h"

extern "C"
JNIEXPORT jlong JNICALL
Java_com_sudox_sockets_SocketClient_createNativeInstance(JNIEnv *env, jobject instance __unused,
                                                         jstring host_, jshort port) {

    const char *host = env->GetStringUTFChars(host_, nullptr);
    auto client = new SocketClient(host, static_cast<uint16_t>(port));
    auto pointer = (long) client;

    return pointer;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sudox_sockets_SocketClient_connect(JNIEnv *env, jobject instance, jlong pointer) {
    ((SocketClient*) pointer)->connect();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sudox_sockets_SocketClient_close(JNIEnv *env, jobject instance, jlong pointer, jboolean error) {
    ((SocketClient*) pointer)->close(error);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_sudox_sockets_SocketClient_opened(JNIEnv *env, jobject instance, jlong pointer) {
    return (jboolean) ((SocketClient*) pointer)->opened();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sudox_sockets_SocketClient_send(JNIEnv *env, jobject instance, jlong pointer, jbyteArray _data) {
    jboolean isCopy;
    auto data = (char*) env->GetByteArrayElements(_data, &isCopy);
    auto dataLength = (unsigned int) env->GetArrayLength(_data);
    auto client = ((SocketClient*) pointer);

    client->send(data, dataLength);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sudox_sockets_SocketClient_sendBuffer(JNIEnv *env, jobject instance, jlong pointer,
                                               jobject _buffer, jint _length) {

    auto buffer = (char*) env->GetDirectBufferAddress(_buffer);
    auto client = ((SocketClient*) pointer);

    client->send(buffer, static_cast<size_t>(_length));
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sudox_sockets_SocketClient_callback(JNIEnv *env, jobject instance, jlong pointer,
                                             jobject _callback) {

    auto client = ((SocketClient*) pointer);
    auto callback = new JNICallback(env, _callback);
    delete client->callback;

    // Binding new callback ...
    client->callback = callback;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_sudox_sockets_SocketClient_readBytes(JNIEnv *env, jobject instance, jlong pointer, jint count) {
    auto client = ((SocketClient*) pointer);
    char *buffer = new char[count];
    ssize_t read = client->read(buffer, static_cast<size_t>(count));

    if (read <= 0) {
        return env->NewByteArray(static_cast<jsize>(0));
    }

    jbyteArray jArray = env->NewByteArray(static_cast<jsize>(read));
    env->SetByteArrayRegion(jArray, 0, static_cast<jsize>(read), reinterpret_cast<const jbyte *>(buffer));

    delete[] buffer;
    return jArray;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_sudox_sockets_SocketClient_availableBytes(JNIEnv *env, jobject instance, jlong pointer) {
    auto client = ((SocketClient*) pointer);
    auto available = client->available();

    return static_cast<jint>(available);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_sudox_sockets_SocketClient_readToBuffer(JNIEnv *env, jobject instance, jlong pointer,
                                                 jobject buffer, jint count, jint offset) {

    auto client = ((SocketClient*) pointer);
    char* bytes = (char*) env->GetDirectBufferAddress(buffer);
    char* bytesWithOffset = &bytes[offset];
    auto read = client->read(bytesWithOffset, static_cast<size_t>(count));

    return static_cast<jint>(read);
}