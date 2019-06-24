#include <jni.h>

#include "SocketClient.h"
#include "callbacks/JNICallback.h"

extern "C"
JNIEXPORT jlong JNICALL
Java_com_sudox_protocol_client_network_SocketClientKt_createNativeInstance(JNIEnv *env, __unused jclass type,
                                                                           jstring host_, jshort port) {
    const char *host = env->GetStringUTFChars(host_, nullptr);
    auto client = new SocketClient(host, static_cast<uint16_t>(port));
    auto pointer = (long) client;

    return pointer;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sudox_protocol_client_network_SocketClientKt_connect(__unused JNIEnv *env, __unused jclass type,
                                                              jlong pointer) {
    ((SocketClient *) pointer)->connect();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sudox_protocol_client_network_SocketClientKt_close(__unused JNIEnv *env, __unused jclass type,
                                                            jlong pointer, jboolean error) {
    ((SocketClient *) pointer)->close(error);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sudox_protocol_client_network_SocketClientKt_send(JNIEnv *env, __unused jclass type,
                                                           jlong pointer, jobject _buffer, jint _length,
                                                           jboolean urgent) {
    auto buffer = (char *) env->GetDirectBufferAddress(_buffer);
    auto client = ((SocketClient *) pointer);

    client->send(buffer, static_cast<size_t>(_length), urgent);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sudox_protocol_client_network_SocketClientKt_callback(JNIEnv *env, __unused jclass type, jlong pointer,
                                                               jobject _callback) {

    auto client = ((SocketClient *) pointer);
    auto callback = new JNICallback(env, _callback);
    delete client->callback;

    // Binding new callback ...
    client->callback = callback;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_sudox_protocol_client_network_SocketClientKt_read0(JNIEnv *env, __unused jclass type, jlong pointer,
                                                           jint count) {
    auto client = ((SocketClient *) pointer);
    char *buffer = new char[count];
    ssize_t read = client->read(buffer, static_cast<size_t>(count));

    if (read <= 0) {
        return nullptr;
    }

    jbyteArray jArray = env->NewByteArray(static_cast<jsize>(read));
    env->SetByteArrayRegion(jArray, 0, static_cast<jsize>(read),
                            reinterpret_cast<const jbyte *>(buffer));

    delete[] buffer;
    return jArray;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_sudox_protocol_client_network_SocketClientKt_read1(JNIEnv *env, __unused jclass type,
                                                            jlong pointer, jobject buffer, jint count, jint
                                                            offset) {
    auto client = ((SocketClient *) pointer);
    char *bytes = (char *) env->GetDirectBufferAddress(buffer);
    char *bytesWithOffset = &bytes[offset];
    auto read = client->read(bytesWithOffset, static_cast<size_t>(count));

    return static_cast<jint>(read);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_sudox_protocol_client_network_SocketClientKt_available(__unused JNIEnv *env, __unused jclass type,
                                                                jlong pointer) {
    auto client = ((SocketClient *) pointer);
    auto available = client->available();

    return static_cast<jint>(available);
}